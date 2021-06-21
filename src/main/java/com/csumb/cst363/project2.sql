create table patient 
(patient_id int(255) not null,
ssn char(13),
name char(25), 
age int(255),
address char(100),
Primary key (name),
unique(ssn)
-- omitted this key because it is easier to make columns unique then reference 
-- PRIMARY KEY(name,ssn)
);

-- ssn has option to be null some patients may forget ssn cards 

insert into patient values

(1,'4443367888','Vernita',21,'5506 Atlas Street, South Pasadena,CA,90032'),
(2,'7897932222','Michael',99,'1000 Mission St., South Pasadena, CA 91030'),
(3,'8798234789','Will',50,'251 North Bristol Avenue, Los Angeles, CA 90049'),
(4,'2432490800','Ferris' ,44,'4160 Country Club Dr., Long Beach, CA 90807'),
(5,'7521343124','Steve',50,'2101 Waverley Street, Palo Alto, CA 94301'),
(6,'0923534522','A. Einstein',70,'707 S Oakland Ave, Pasadena, CA 91106'),
(7,'3028579453','R. Feynman',70,' 895 Altadena Dr, Altadena, CA 91001'),
(8,'6753254344','M. Jackson',50,'100 Carlwood Drive, Beverly Hills, CA'),
(9,'8942594324','C. Eastwood', 99, '846 Stradella Road, CA')

;
create table doctor
(
doc_id int(255) not null,
ssn char(13),
name char(25) not null, 
speciality char(100) not null,
yearExperience int(255),
Primary key(doc_id),
unique(ssn),
unique(name)
-- Primary key(ssn,name)
);

insert into doctor values 

(1,'2224448888','Schwartz','Oncology',10),
(2,'2994995999','House','Diagnostic Medicine',30),
(3,'8885235432','Price','Cardiology',2),
(4,'5234589342','Davis','Allergies',5),
(5,'2341123333','Talwar','Pediatrics',4)

;
create table drug 
(drug_id int(10) not null,
trade_name char(50),
formula char(50),
drug_price float(2),
primary key (drug_id),
unique(trade_name)
);

insert into drug values 
(01,'Parlodel','C32H40BrN5O5',33.91), -- oncology
(02,'Dostinex','C26H37N5O2',235.63),
(03,'Comirnaty','BNT162b2',37.00), -- covid
(04,'Advil','C13H18O2',.31), -- headache
(05,'Sectral','C18H28N2O4',5.18), -- cardiology
(06,'Tenormin','C14H22N2O3',13.89),
(07,'Zyrtec','C21H25ClN2O32HCl',.87), -- allergies
(08,'Allegra','C32H39NO4',1.94), -- allergies
(09,'Ibuprofen','C13H18O2',.40), -- children 
(10,'Amoxicillin','C16H19N3O5S',.49); -- antibiotic

-- date filled
create table pharmacy
(pharmacy_id char(10) ,
pharmacy_name char(50) unique ,
address char(100) unique ,
phone char(20) unique,
primary key (pharmacy_id));

insert into pharmacy values 
('WAL','Walgreens','Sesame St.','111-222-3333'),
('RIT','Rite Aid','Elm St.','222-333-4444'),
('CVS','CVS Pharmacy','Alphabet St.','333-444-5555');


create table prescription
(
rxid int(255) AUTO_INCREMENT,  -- rxid 
doctor_ssn char(13),
doctorName char(25) ,	
patient_ssn char(13) ,
patientName char(25),
drugName char(50) ,
quantity int(100),
pharmacy_id char(10) ,
pharmacy_name char(50) ,
address char(100) ,
pharmacyphone char(20),
primary key (rxid),
foreign key(doctor_ssn) references doctor(ssn),
foreign key(patientname) references patient(name),
foreign key(patient_ssn) references patient(ssn),
foreign key(drugName) references drug(trade_name),
foreign key(pharmacy_id) references pharmacy(pharmacy_id),
foreign key(pharmacy_name) references pharmacy(pharmacy_name),
foreign key(address) references pharmacy(address),
foreign key(pharmacyphone) references pharmacy(phone)
);