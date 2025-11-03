package com.babycash.backend.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "El contenido del comentario no puede estar vacío")
    @Size(min = 1, max = 2000, message = "El comentario debe tener entre 1 y 2000 caracteres")
    private String content;

    private Long parentCommentId; // null for top-level comments, set for replies
}
