package com.mewebstudio.javaspringbootboilerplate.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import com.mewebstudio.javaspringbootboilerplate.dto.request.auth.RegisterRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.auth.ResetPasswordRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.AbstractBaseCreateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.AbstractBaseUpdateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.CreateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.UpdatePasswordRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.UpdateProfileRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.UpdateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.entity.Anuncio;
import com.mewebstudio.javaspringbootboilerplate.entity.InteressesUsuario;
import com.mewebstudio.javaspringbootboilerplate.entity.PrivacidadePerfil;
import com.mewebstudio.javaspringbootboilerplate.entity.TipoUsuario;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.entity.specification.UserFilterSpecification;
import com.mewebstudio.javaspringbootboilerplate.entity.specification.criteria.PaginationCriteria;
import com.mewebstudio.javaspringbootboilerplate.entity.specification.criteria.UserCriteria;
import com.mewebstudio.javaspringbootboilerplate.event.UserEmailVerificationSendEvent;
import com.mewebstudio.javaspringbootboilerplate.event.UserPasswordResetSendEvent;
import com.mewebstudio.javaspringbootboilerplate.exception.BadRequestException;
import com.mewebstudio.javaspringbootboilerplate.exception.ForbiddenException;
import com.mewebstudio.javaspringbootboilerplate.exception.NotFoundException;
import com.mewebstudio.javaspringbootboilerplate.repository.AnuncioRepository;
import com.mewebstudio.javaspringbootboilerplate.repository.UserRepository;
import com.mewebstudio.javaspringbootboilerplate.security.JwtUserDetails;
import com.mewebstudio.javaspringbootboilerplate.util.Constants;
import com.mewebstudio.javaspringbootboilerplate.util.PageRequestBuilder;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final AnuncioRepository anuncioRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    private final EmailVerificationTokenService emailVerificationTokenService;

    private final PasswordResetTokenService passwordResetTokenService;

    private final ApplicationEventPublisher eventPublisher;

    private final MessageSourceService messageSourceService;

    /**
     * Get authentication.
     *
     * @return Authentication
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Return the authenticated user.
     *
     * @return user User
     */
    public User getUser() {
        Authentication authentication = getAuthentication();
        if (authentication.isAuthenticated()) {
            try {
                return findById(getPrincipal(authentication).getId());
            } catch (ClassCastException | NotFoundException e) {
                log.warn("[JWT] User details not found!");
                throw new BadCredentialsException(messageSourceService.get("bad_credentials"));
            }
        } else {
            log.warn("[JWT] User not authenticated!");
            throw new BadCredentialsException(messageSourceService.get("bad_credentials"));
        }
    }

    /**
     * Count users.
     *
     * @return Long
     */
    public long count() {
        return userRepository.count();
    }

    /**
     * Find all users with pagination.
     *
     * @param criteria           UserCriteria
     * @param paginationCriteria PaginationCriteria
     * @return Page
     */
    public Page<User> findAll(UserCriteria criteria, PaginationCriteria paginationCriteria) {
        return userRepository.findAll(new UserFilterSpecification(criteria),
            PageRequestBuilder.build(paginationCriteria));
    }

    /**
     * Find a user by id.
     *
     * @param id UUID
     * @return User
     */
    public User findById(UUID id) {
        return userRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                new String[]{messageSourceService.get("user")})));
    }

    /**
     * Find a user by id.
     *
     * @param id String
     * @return User
     */
    public User findById(String id) {
        return findById(UUID.fromString(id));
    }

    /**
     * Find a user by email.
     *
     * @param email String.
     * @return User
     */
    public User findByEmail(final String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                new String[]{messageSourceService.get("user")})));
    }

    /**
     * Load user details by username.
     *
     * @param email String
     * @return UserDetails
     * @throws UsernameNotFoundException email not found exception.
     */
    public UserDetails loadUserByEmail(final String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                new String[]{messageSourceService.get("user")})));

        return JwtUserDetails.create(user);
    }

    /**
     * Loads user details by UUID string.
     *
     * @param id String
     * @return UserDetails
     */
    public UserDetails loadUserById(final String id) {
        User user = userRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                new String[]{messageSourceService.get("user")})));

        return JwtUserDetails.create(user);
    }

    /**
     * Get UserDetails from security context.
     *
     * @param authentication Wrapper for security context
     * @return the Principal being authenticated or the authenticated principal after authentication.
     */
    public JwtUserDetails getPrincipal(final Authentication authentication) {
        return (JwtUserDetails) authentication.getPrincipal();
    }

    /**
     * Register user.
     *
     * @param request RegisterRequest
     * @return User
     */
    public User register(final RegisterRequest request) throws BindException {
        log.info("Registering user with email: {}", request.getEmail());

        User user = createUser(request);
        user.setRoles(List.of(roleService.findByName(Constants.RoleEnum.USER)));
        userRepository.save(user);

        emailVerificationEventPublisher(user);

        log.info("User registered with email: {}, {}", user.getEmail(), user.getId());

        return user;
    }

    /**
     * Create user.
     *
     * @param request CreateUserRequest
     * @return User
     */
    public User create(final CreateUserRequest request) throws BindException {
        log.info("Creating user with email: {}", request.getEmail());

        User user = createUser(request);
        request.getRoles().forEach(role -> user.getRoles()
            .add(roleService.findByName(Constants.RoleEnum.get(role))));

        if (request.getIsEmailVerified() != null && request.getIsEmailVerified()) {
            user.setEmailVerifiedAt(LocalDateTime.now());
        }

        if (request.getIsBlocked() != null && request.getIsBlocked()) {
            user.setBlockedAt(LocalDateTime.now());
        }

        userRepository.save(user);

        log.info("User created with email: {}, {}", user.getEmail(), user.getId());

        return user;
    }

    /**
     * Update user.
     *
     * @param id      UUID
     * @param request UpdateUserRequest
     * @return User
     */
    public User update(UUID id, UpdateUserRequest request) throws BindException {
        User user = findById(id);
        user.setEmail(request.getEmail());
        user.setName(request.getName());

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoles() != null) {
            user.setRoles(request.getRoles().stream()
                .map(role -> roleService.findByName(Constants.RoleEnum.get(role)))
                .collect(Collectors.toList()));
        }

        if (request.getIsEmailVerified() != null) {
            if (request.getIsEmailVerified()) {
                user.setEmailVerifiedAt(LocalDateTime.now());
            } else {
                user.setEmailVerifiedAt(null);
            }
        }

        return updateUser(user, request);
    }

    /**
     * Update user.
     *
     * @param id      String
     * @param request UpdateUserRequest
     * @return User
     */
    public User update(String id, UpdateUserRequest request) throws BindException {
        return update(UUID.fromString(id), request);
    }

    /**
     * Update user password.
     *
     * @param request UpdatePasswordRequest
     */
    public User updatePassword(UpdatePasswordRequest request) throws BindException {
        User user = getUser();
        log.info("Updating password for user with email: {}", user.getEmail());

        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "oldPassword",
                messageSourceService.get("invalid_old_password")));
        }

        if (request.getOldPassword().equals(request.getPassword())) {
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "password",
                messageSourceService.get("same_password_error")));
        }

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        log.info("Password updated for user with email: {}", user.getEmail());

        return user;
    }

    /**
     * Reset password.
     *
     * @param token String
     * @param request ResetPasswordRequest
     */
    public void resetPassword(String token, ResetPasswordRequest request) {
        User user = passwordResetTokenService.getUserByToken(token);
        log.info("Resetting password for user with email: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        passwordResetTokenService.deleteByUserId(user.getId());
        log.info("Password reset for user with email: {}", user.getEmail());
    }

    /**
     * Resend e-mail verification mail.
     */
    public void resendEmailVerificationMail() {
        User user = getUser();
        log.info("Resending e-mail verification mail to email: {}", user.getEmail());
        if (user.getEmailVerifiedAt() != null) {
            throw new BadRequestException(messageSourceService.get("your_email_already_verified"));
        }

        emailVerificationEventPublisher(user);
        log.info("Resending e-mail verification mail to email: {}", user.getEmail());
    }

    /**
     * E-mail verification.
     *
     * @param token String
     */
    public void verifyEmail(String token) {
        log.info("Verifying e-mail with token: {}", token);
        User user = emailVerificationTokenService.getUserByToken(token);
        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);

        emailVerificationTokenService.deleteByUserId(user.getId());
        log.info("E-mail verified with token: {}", token);
    }

    /**
     * Send password reset mail.
     *
     * @param email String
     */
    public void sendEmailPasswordResetMail(String email) {
        log.info("Sending password reset mail to email: {}", email);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                new String[]{messageSourceService.get("user")})));

        passwordResetEventPublisher(user);
        log.info("Password reset mail sent to email: {}", email);
    }

    /**
     * Delete user.
     *
     * @param id UUID
     */
    public void delete(String id) {
        userRepository.delete(findById(id));
    }

    /**
     * Create user.
     *
     * @param request AbstractBaseCreateUserRequest
     * @return User
     */
    private User createUser(AbstractBaseCreateUserRequest request) throws BindException {
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        userRepository.findByEmail(request.getEmail())
            .ifPresent(user -> {
                log.error("User with email: {} already exists", request.getEmail());
                bindingResult.addError(new FieldError(bindingResult.getObjectName(), "email",
                    messageSourceService.get("unique_email")));
            });

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        User.UserBuilder userBuilder = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .name(request.getName()); 

        if (StringUtils.hasText(request.getTipoUsuario())) {
            userBuilder.tipoUsuario(TipoUsuario.valueOf(request.getTipoUsuario()));
        }

        return userBuilder.build();
    }

    /**
     * Update user.
     *
     * @param user    User
     * @param request UpdateUserRequest
     * @return User
     */
    private User updateUser(User user, AbstractBaseUpdateUserRequest request) throws BindException {
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        if (!user.getEmail().equals(request.getEmail()) &&
            userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "email",
                messageSourceService.get("already_exists")));
        }

        boolean isRequiredEmailVerification = false;
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            user.setEmail(request.getEmail());
            user.setEmailVerificationToken(emailVerificationTokenService.create(user));
            isRequiredEmailVerification = true;
        }

        if (StringUtils.hasText(request.getName()) && !request.getName().equals(user.getName())) {
            user.setName(request.getName());
        }


        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        userRepository.save(user);

        if (isRequiredEmailVerification) {
            emailVerificationEventPublisher(user);
        }

        return user;
    }

    /**
     * Update user profile.
     *
     * @param request UpdateProfileRequest
     * @return User
     * @throws BindException if validation fails
     */
    public User updateProfile(final UpdateProfileRequest request) throws BindException {
        User user = getUser();
        log.info("Updating profile for user with id: {}", user.getId());

        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        boolean emailChangedAndRequiresVerification = false;

        // Atualizar nome (herdado de AbstractBaseUpdateUserRequest)
        if (StringUtils.hasText(request.getName()) && !Objects.equals(user.getName(), request.getName())) {
            user.setName(request.getName());
        }

        // Atualizar e-mail (herdado de AbstractBaseUpdateUserRequest)
        if (StringUtils.hasText(request.getEmail()) && !Objects.equals(user.getEmail(), request.getEmail())) {
            userRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(user.getId())) {
                    bindingResult.addError(new FieldError(bindingResult.getObjectName(), "email",
                        messageSourceService.get("unique_email")));
                }
            });
            if (!bindingResult.hasFieldErrors("email")) {
                user.setEmail(request.getEmail());
                user.setEmailVerifiedAt(null); // Requer nova verificação
                emailChangedAndRequiresVerification = true;
            }
        }

        // Atualizar senha
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Atualizar semestre
        if (request.getSemestre() != null && !Objects.equals(user.getSemestre(), request.getSemestre())) {
            user.setSemestre(request.getSemestre());
        }

        // Atualizar tipoUsuario
        if (StringUtils.hasText(request.getTipoUsuario())) {
            try {
                TipoUsuario requestedTipoUsuario = TipoUsuario.valueOf(request.getTipoUsuario());
                if (!Objects.equals(user.getTipoUsuario(), requestedTipoUsuario)) {
                    user.setTipoUsuario(requestedTipoUsuario);
                }
            } catch (IllegalArgumentException e) {
                bindingResult.addError(new FieldError(bindingResult.getObjectName(), "tipoUsuario",
                    messageSourceService.get("invalid_enum_value", new String[]{request.getTipoUsuario()})));
            }
        }

        // Atualizar biografia
        if (request.getBiografia() != null && !Objects.equals(user.getBiografia(), request.getBiografia())) {
            user.setBiografia(request.getBiografia());
        }

        // Atualizar curso
        if (request.getCurso() != null && !Objects.equals(user.getCurso(), request.getCurso())) {
            user.setCurso(request.getCurso());
        }

        // Atualizar regiaoDeInteresse
        if (request.getRegiaoDeInteresse() != null && !Objects.equals(user.getRegiaoDeInteresse(), request.getRegiaoDeInteresse())) {
            user.setRegiaoDeInteresse(request.getRegiaoDeInteresse());
        }

        // Atualizar os interesses do usuário
        if (request.getInteresses() != null) {
            user.setInteresses(
                request.getInteresses().stream()
                    .map(String::toUpperCase)
                    .map(InteressesUsuario::valueOf)
                    .collect(Collectors.toSet())
            );
        }

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        if (emailChangedAndRequiresVerification) {
            user.setEmailVerificationToken(emailVerificationTokenService.create(user));
        }

        User updatedUser = userRepository.save(user);

        if (emailChangedAndRequiresVerification) {
            eventPublisher.publishEvent(new UserEmailVerificationSendEvent(this, updatedUser));
            log.info("Email changed for user id: {}. Verification email sent.", updatedUser.getId());
        }

        log.info("Profile updated for user with id: {}", updatedUser.getId());
        return updatedUser;
    }

    /**
     * Updates the profile photo for the current user.
     * @param file The image file to upload.
     * @throws IOException if an I/O error occurs.
     */
    public void updateProfilePhoto(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty or not provided.");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new BadRequestException("Invalid file type. Only images are allowed.");
        }

        User user = getUser();
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
        user.setFotoPerfil(base64Image);
        userRepository.save(user);
        log.info("Profile photo updated for user [{}]", user.getId());
    }

    /**
     * Favorites or unfavorites an announcement for the current user.
     *
     * @param anuncioId The ID of the announcement to toggle.
     * @return true if the announcement is now a favorite, false otherwise.
     */
    public boolean alternarAnuncioFavorito(UUID anuncioId) {
        User usuario = getUser();
        Anuncio anuncio = anuncioRepository.findById(anuncioId)
            .orElseThrow(() -> new NotFoundException(messageSourceService.get("not_found_with_param",
                new String[]{"Anúncio"})));

        boolean favoritado;
        if (usuario.getAnunciosFavoritos().contains(anuncio)) {
            // Se já for favorito, remove
            usuario.getAnunciosFavoritos().remove(anuncio);
            favoritado = false;
            log.info("User [{}] unfavorited announcement [{}]", usuario.getId(), anuncioId);
        } else {
            // Se não for favorito, adiciona
            usuario.getAnunciosFavoritos().add(anuncio);
            favoritado = true;
            log.info("User [{}] favorited announcement [{}]", usuario.getId(), anuncioId);
        }

        userRepository.save(usuario);
        return favoritado;
    }

    /**
     * Gets the list of favorited announcements for the current user.
     *
     * @return A set of favorited Anuncio entities.
     */
    public Set<Anuncio> getAnunciosFavoritos() {
        User usuario = getUser();
        log.info("Fetching favorite announcements for user [{}]", usuario.getId());
        return usuario.getAnunciosFavoritos();
    }

    /**
     * E-mail verification event publisher.
     *
     * @param user User
     */
    protected void emailVerificationEventPublisher(User user) {
        user.setEmailVerificationToken(emailVerificationTokenService.create(user));
        eventPublisher.publishEvent(new UserEmailVerificationSendEvent(this, user));
    }

    /**
     * Password reset event publisher.
     *
     * @param user User
     */
    private void passwordResetEventPublisher(User user) {
        user.setPasswordResetToken(passwordResetTokenService.create(user));
        eventPublisher.publishEvent(new UserPasswordResetSendEvent(this, user));
    }

    /**
     * Find user by id with privacy check.
     *
     * @param id UUID
     * @return User
     * @throws NotFoundException if user not found
     * @throws ForbiddenException if user profile is private or access is restricted
     */
    public User findByIdWithPrivacyCheck(UUID id) {
        User targetUser = findById(id);
        User currentUser = null;
        
        try {
            currentUser = getUser();
        } catch (BadCredentialsException e) {
            // Usuário não autenticado
        }

        // Se for o próprio usuário, permite acesso
        if (currentUser != null && currentUser.getId().equals(targetUser.getId())) {
            return targetUser;
        }

        // Verifica as regras de privacidade
        switch (targetUser.getPrivacidadePerfil()) {
            case PRIVADO:
                throw new ForbiddenException(messageSourceService.get("profile_is_private"));
            
            case APENAS_LOCADORES:
                if (currentUser == null || 
                    currentUser.getRoles().stream()
                        .noneMatch(role -> role.getName().name().equals("LOCADOR"))) {
                    throw new ForbiddenException(messageSourceService.get("profile_only_visible_to_locadores"));
                }
                break;
            
            case PUBLICO:
            default:
                // Permite acesso
                break;
        }

        return targetUser;
    }

    /**
     * Update user profile privacy.
     *
     * @param privacidadePerfil String
     * @return User
     */
    public User updateProfilePrivacy(String privacidadePerfil) {
        User user = getUser();
        try {
            PrivacidadePerfil novaPrivacidade = PrivacidadePerfil.valueOf(privacidadePerfil.toUpperCase());
            user.setPrivacidadePerfil(novaPrivacidade);
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(messageSourceService.get("invalid_enum_value", 
                new String[]{privacidadePerfil}));
        }
    }
}
