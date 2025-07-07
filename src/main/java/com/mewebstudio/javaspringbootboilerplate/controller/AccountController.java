package com.mewebstudio.javaspringbootboilerplate.controller;

import static com.mewebstudio.javaspringbootboilerplate.util.Constants.SECURITY_SCHEME_NAME;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mewebstudio.javaspringbootboilerplate.dto.request.user.UpdatePasswordRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.UpdatePrivacyRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.request.user.UpdateProfileRequest;
import com.mewebstudio.javaspringbootboilerplate.dto.response.AnuncioResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.DetailedErrorResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.ErrorResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.SuccessResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.user.UserResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.service.MessageSourceService;
import com.mewebstudio.javaspringbootboilerplate.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
@Tag(name = "002. Account", description = "Account API")
public class AccountController extends AbstractBaseController {
    private final UserService userService;

    private final MessageSourceService messageSourceService;

    @GetMapping("/me")
    @Operation(
        summary = "Me endpoint",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Bad credentials",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(UserResponse.convert(userService.getUser()));
    }

    @PatchMapping("/profile")
    @Operation(
        summary = "Update user profile endpoint",
        description = "Allows the authenticated user to update their profile information. " +
                      "Only include the fields you want to update in the request body.",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful operation, profile updated",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad request (e.g., validation errors for provided fields)",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = DetailedErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized, authentication required",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "422",
                description = "Unprocessable Entity (e.g., validation errors like unique email constraint)",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = DetailedErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<UserResponse> updateProfile(
        @Parameter(description = "Request body to update user profile. Only include fields to be changed.", required = true)
        @RequestBody @Valid UpdateProfileRequest request
    ) throws BindException {
        User updatedUser = userService.updateProfile(request);
        return ResponseEntity.ok(UserResponse.convert(updatedUser));
    }

    @PostMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Update user profile photo",
        description = "Uploads and updates the profile photo for the authenticated user.",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    )
    public ResponseEntity<SuccessResponse> updateProfilePhoto(
        @Parameter(description = "Image file to be uploaded", required = true)
        @RequestParam("file") MultipartFile file
    ) throws IOException {
        userService.updateProfilePhoto(file);
        return ResponseEntity.ok(SuccessResponse.builder()
            .message(messageSourceService.get("profile_photo_updated"))
            .build());
    }

    @PostMapping("/password")
    @Operation(
        summary = "Password update endpoint",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SuccessResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Bad credentials",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "422",
                description = "Validation failed",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = DetailedErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<SuccessResponse> password(
        @Parameter(description = "Request body to update password", required = true)
        @RequestBody @Valid UpdatePasswordRequest request
    ) throws BindException {
        userService.updatePassword(request);

        return ResponseEntity.ok(SuccessResponse.builder()
            .message(messageSourceService.get("your_password_updated"))
            .build());
    }

    @GetMapping("/resend-email-verification")
    @Operation(
        summary = "Resend e-mail verification endpoint",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SuccessResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Bad credentials",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<SuccessResponse> resendEmailVerificationMail() {
        userService.resendEmailVerificationMail();

        return ResponseEntity.ok(SuccessResponse.builder()
            .message(messageSourceService.get("verification_email_sent"))
            .build());
    }

    @GetMapping("/favoritos")
    @Operation(
        summary = "Get favorite announcements",
        description = "Returns a list of announcements favorited by the user.",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful operation. Returns a list of announcements.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AnuncioResponse.class))
            )
        }
    )
    public ResponseEntity<List<AnuncioResponse>> getAnunciosFavoritos() {
        List<AnuncioResponse> favoritos = userService.getAnunciosFavoritos()
            .stream()
            .map(AnuncioResponse::convert)
            .collect(Collectors.toList());
        return ResponseEntity.ok(favoritos);
    }

    @PostMapping("/favoritar/{anuncioId}")
    @Operation(
        summary = "Toggle favorite announcement",
        description = "Adds or removes an announcement from the user's favorites list.",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful operation. Returns the current favorite status.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(type = "object", example = "{\"favorited\": true}"))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Announcement not found",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    public ResponseEntity<Map<String, Boolean>> marcarFavorito(
        @Parameter(description = "ID of the announcement to favorite/unfavorite", required = true)
        @PathVariable UUID anuncioId
    ) {
        boolean isFavorited = userService.alternarAnuncioFavorito(anuncioId);
        return ResponseEntity.ok(Map.of("favorited", isFavorited));
    }

    @GetMapping("/user/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Returns user information by ID, respecting privacy settings",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden - Profile is private or restricted",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<UserResponse> getUserById(
        @Parameter(description = "UUID of the user to retrieve", required = true)
        @PathVariable UUID id
    ) {
        return ResponseEntity.ok(UserResponse.convert(userService.findByIdWithPrivacyCheck(id)));
    }

    @PutMapping("/privacy")
    @Operation(
        summary = "Update profile privacy settings",
        description = "Updates the privacy settings of the authenticated user's profile",
        security = @SecurityRequirement(name = SECURITY_SCHEME_NAME),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad request - Invalid privacy type",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<UserResponse> updatePrivacy(
        @Parameter(description = "Request body to update privacy settings", required = true)
        @RequestBody @Valid UpdatePrivacyRequest request
    ) {
        return ResponseEntity.ok(UserResponse.convert(userService.updateProfilePrivacy(request.getPrivacidadePerfil())));
    }

}
