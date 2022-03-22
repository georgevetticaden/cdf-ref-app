create database employees_hr_hive_db;

CREATE TABLE employees_hr_hive_db.driver
(driverid BIGINT,
drivername string,
certified string,
wage_plan string
)
CLUSTERED BY (driverId) INTO 5 BUCKETS
STORED AS ORC
TBLPROPERTIES(
'transactional'='false'
);


insert into employees_hr_hive_db.driver
values
(10,"George Vetticaden","N","miles"),
(11,"Jamie Engesser","N","miles"),
(12,"Paul Codding","Y","hours"),
(13,"Joe Niemiec","Y","hours"),
(14,"Adis Cesir","Y","hours"),
(15,"Rohit Bakshi","Y","hours"),
(16,"Tom McCuch","Y","hours"),
(17,"Eric Mizell","Y","hours"),
(18,"Grant Liu","Y","hours"),
(19,"Ajay Singh","Y","hours"),
(20,"Chris Harris","Y","hours"),
(21,"Jeff Markham","Y","hours"),
(22,"Nadeem Asghar","Y","hours"),
(23,"Adam Diaz","Y","hours"),
(24,"Don Hilborn","Y","hours"),
(25,"Jean-Philippe Playe","Y","hours"),
(26,"Michael Aube","Y","hours"),
(27,"Mark Lochbihler","Y","hours"),
(28,"Olivier Renault","Y","hours"),
(29,"Teddy Choi","Y","hours"),
(30,"Dan Rice","Y","hours"),
(31,"Rommel Garcia","Y","hours"),
(32,"Ryan Templeton","Y","hours"),
(33,"Sridhara Sabbella","Y","hours"),
(34,"Frank Romano","Y","hours"),
(35,"Emil Siemes","Y","hours"),
(36,"Andrew Grande","Y","hours"),
(37,"Wes Floyd","Y","hours"),
(38,"Scott Shaw","Y","hours"),
(39,"David Kaiser","Y","hours"),
(40,"Nicolas Maillard","Y","hours"),
(41,"Greg Phillips","Y","hours"),
(42,"Randy Gelhausen","Y","hours"),
(43,"Dave Patton","Y","hours")


select * from employees_hr_hive_db.driver;