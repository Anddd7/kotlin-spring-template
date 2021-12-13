TRUNCATE TABLE user_info CASCADE;
TRUNCATE TABLE employee CASCADE;

INSERT INTO user_info
VALUES (1, 'user1@order.com'),
       (2, 'user2@marketing.com'),
       (3, 'user3@sales.com'),
       (4, 'user4@delivery.com');

INSERT INTO employee
VALUES (1, 'Admin', 999),
       (10001, 'ORDER', 1),
       (10002, 'MARKETING', 2),
       (10003, 'SALES', 3),
       (10004, 'DELIVERY', 4);
