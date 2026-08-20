package com.smartspend.category.repository;

import com.smartspend.category.entity.Category;
import com.smartspend.category.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    @Query("""
            select c
            from Category c
            where c.defaultCategory = true
               or c.user.id = :userId
            order by
                c.defaultCategory desc,
                c.type asc,
                c.name asc
            """)
    List<Category> findAvailableCategories(
            @Param("userId") Long userId
    );

    @Query("""
            select c
            from Category c
            where (
                    c.defaultCategory = true
                    or c.user.id = :userId
                  )
              and c.type = :type
            order by
                c.defaultCategory desc,
                c.name asc
            """)
    List<Category> findAvailableCategoriesByType(
            @Param("userId") Long userId,
            @Param("type") CategoryType type
    );

    @Query("""
            select c
            from Category c
            where c.id = :categoryId
              and (
                    c.defaultCategory = true
                    or c.user.id = :userId
                  )
            """)
    Optional<Category> findAccessibleById(
            @Param("categoryId") Long categoryId,
            @Param("userId") Long userId
    );

    @Query("""
            select c
            from Category c
            where c.id = :categoryId
              and c.user.id = :userId
              and c.defaultCategory = false
            """)
    Optional<Category> findOwnedCategory(
            @Param("categoryId") Long categoryId,
            @Param("userId") Long userId
    );

    boolean existsByUserIdAndNameIgnoreCaseAndType(
            Long userId,
            String name,
            CategoryType type
    );

    @Query("""
            select case when count(c) > 0 then true else false end
            from Category c
            where c.user.id = :userId
              and lower(c.name) = lower(:name)
              and c.type = :type
              and c.id <> :categoryId
            """)
    boolean existsDuplicateForUpdate(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("type") CategoryType type,
            @Param("categoryId") Long categoryId
    );

    @Query(
            value = """
                    SELECT COUNT(*)
                    FROM transactions
                    WHERE category_id = :categoryId
                      AND deleted_at IS NULL
                    """,
            nativeQuery = true
    )
    long countActiveTransactionsByCategoryId(
            @Param("categoryId") Long categoryId
    );
}