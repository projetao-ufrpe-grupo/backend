package com.mewebstudio.javaspringbootboilerplate.controller;

import com.mewebstudio.javaspringbootboilerplate.dto.response.ErrorResponse;
import com.mewebstudio.javaspringbootboilerplate.dto.response.user.UserResponse;
import com.mewebstudio.javaspringbootboilerplate.entity.User;
import com.mewebstudio.javaspringbootboilerplate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "005. Users", description = "Public User API")
public class UsersController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(
        summary = "Get public user details by ID",
        description = "Returns the details of a specific user if their profile is public. " +
                      "If the profile is private or restricted, it will result in an error.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful operation, user details returned",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden, the user's profile is not public",
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
            )
        }
    )
    public ResponseEntity<UserResponse> getPublicUserProfile(
        @Parameter(description = "ID of the user to retrieve", required = true)
        @PathVariable("id") UUID id
    ) {
        User user = userService.findByIdWithPrivacyCheck(id);
        return ResponseEntity.ok(UserResponse.convert(user));
    }
}