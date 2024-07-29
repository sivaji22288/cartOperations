INSERT INTO product (id,name,category,price,available_quantity) VALUES (1,'Rice',5,100.0,200);
INSERT INTO product (id,name,category,price,available_quantity) VALUES (2,'Wheat',5,120.0,200);
INSERT INTO product (id,name,category,price,available_quantity) VALUES (3,'TV',0,10000.0,10);
INSERT INTO product (id,name,category,price,available_quantity) VALUES (4,'Ball',3,10.0,200);
INSERT INTO product (id,name,category,price,available_quantity) VALUES (5,'Chair',2,250.0,200);

INSERT INTO retail_user (id,name,email,is_employee,is_affiliated,registered_on) VALUES (1,'John Employee','employee@tmail.com',true,false,'2020-01-01');
INSERT INTO retail_user (id,name,email,is_employee,is_affiliated,registered_on) VALUES (2,'Affiliated','affiliated@tmail.com',false,true,'2020-01-01');
INSERT INTO retail_user (id,name,email,is_employee,is_affiliated,registered_on) VALUES (3,'Old user','old@tmail.com',false,false,'2020-01-01');
INSERT INTO retail_user (id,name,email,is_employee,is_affiliated,registered_on) VALUES (4,'New User','new@tmail.com',true,false,'2024-01-01');

INSERT INTO cart (id,retail_user_id,total_cost,bill_discount,user_discount) VALUES (1,1,2020.0,0,0);
INSERT INTO cart (id,retail_user_id,total_cost,bill_discount,user_discount) VALUES (2,2,0,0,0);
INSERT INTO cart (id,retail_user_id,total_cost,bill_discount,user_discount) VALUES (3,3,0,0,0);
INSERT INTO cart (id,retail_user_id,total_cost,bill_discount,user_discount) VALUES (4,4,0,0,0);

INSERT INTO item (id,name,category,price,quantity,product_id,cart_id,discount) VALUES (51,'Rice',5,100.0,20,1,1,0);
INSERT INTO item (id,name,category,price,quantity,product_id,cart_id,discount) VALUES (52,'Ball',3,10.0,2,4,1,0);