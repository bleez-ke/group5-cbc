TYPE=VIEW
query=select `cbc_assessment_system`.`users`.`id` AS `id`,`cbc_assessment_system`.`users`.`username` AS `username`,`cbc_assessment_system`.`users`.`firstName` AS `firstName`,`cbc_assessment_system`.`users`.`lastName` AS `lastName`,`cbc_assessment_system`.`users`.`gender` AS `gender`,`cbc_assessment_system`.`users`.`bio` AS `bio`,`cbc_assessment_system`.`users`.`role` AS `role`,`cbc_assessment_system`.`users`.`password` AS `password`,`cbc_assessment_system`.`users`.`occupation` AS `occupation`,`cbc_assessment_system`.`users`.`title` AS `title`,`cbc_assessment_system`.`users`.`class` AS `class`,`cbc_assessment_system`.`users`.`parentName` AS `parentName`,`cbc_assessment_system`.`users`.`location` AS `location` from `cbc_assessment_system`.`users` where `cbc_assessment_system`.`users`.`role` = \'student\'
md5=1cfebecde97594b7286c4b5d5c246435
updatable=1
algorithm=0
definer_user=root
definer_host=localhost
suid=2
with_check_option=0
timestamp=0001731741341945217
create-version=2
source=SELECT *\nFROM users\nWHERE role = \'student\'
client_cs_name=utf8mb3
connection_cl_name=utf8mb3_general_ci
view_body_utf8=select `cbc_assessment_system`.`users`.`id` AS `id`,`cbc_assessment_system`.`users`.`username` AS `username`,`cbc_assessment_system`.`users`.`firstName` AS `firstName`,`cbc_assessment_system`.`users`.`lastName` AS `lastName`,`cbc_assessment_system`.`users`.`gender` AS `gender`,`cbc_assessment_system`.`users`.`bio` AS `bio`,`cbc_assessment_system`.`users`.`role` AS `role`,`cbc_assessment_system`.`users`.`password` AS `password`,`cbc_assessment_system`.`users`.`occupation` AS `occupation`,`cbc_assessment_system`.`users`.`title` AS `title`,`cbc_assessment_system`.`users`.`class` AS `class`,`cbc_assessment_system`.`users`.`parentName` AS `parentName`,`cbc_assessment_system`.`users`.`location` AS `location` from `cbc_assessment_system`.`users` where `cbc_assessment_system`.`users`.`role` = \'student\'
mariadb-version=110403
