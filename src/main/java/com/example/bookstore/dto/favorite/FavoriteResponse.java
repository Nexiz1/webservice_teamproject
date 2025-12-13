package com.example.bookstore.dto.favorite;

import com.example.bookstore.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {

    private Long favoriteId;
    private Long userId;
    private Long bookId;

    public static FavoriteResponse from(Favorite favorite) {
        return FavoriteResponse.builder()
                .favoriteId(favorite.getId())
                .userId(favorite.getUser().getId())
                .bookId(favorite.getBook().getId())
                .build();
    }
}
