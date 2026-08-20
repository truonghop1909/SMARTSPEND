INSERT INTO categories (
    user_id,
    name,
    type,
    icon,
    color,
    is_default
)
VALUES
    (NULL, 'Lương', 'INCOME', 'wallet', '#22C55E', TRUE),
    (NULL, 'Thưởng', 'INCOME', 'gift', '#10B981', TRUE),
    (NULL, 'Đầu tư', 'INCOME', 'chart-line', '#14B8A6', TRUE),
    (NULL, 'Thu nhập khác', 'INCOME', 'circle-plus', '#06B6D4', TRUE),
    (NULL, 'Ăn uống', 'EXPENSE', 'utensils', '#F59E0B', TRUE),
    (NULL, 'Di chuyển', 'EXPENSE', 'car', '#3B82F6', TRUE),
    (NULL, 'Mua sắm', 'EXPENSE', 'shopping-bag', '#EC4899', TRUE),
    (NULL, 'Giải trí', 'EXPENSE', 'gamepad', '#8B5CF6', TRUE),
    (NULL, 'Sức khỏe', 'EXPENSE', 'heart-pulse', '#EF4444', TRUE),
    (NULL, 'Giáo dục', 'EXPENSE', 'graduation-cap', '#6366F1', TRUE),
    (NULL, 'Hóa đơn', 'EXPENSE', 'receipt', '#F97316', TRUE),
    (NULL, 'Chi tiêu khác', 'EXPENSE', 'ellipsis', '#64748B', TRUE);