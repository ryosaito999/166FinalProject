
COPY USR(
	userId,
	password,
	email,
	name,
	dateOfBirth)
FROM '/home/csmajs/rsait001/final166/data/usr.csv'
WITH DELIMITER ';';

COPY WORK_EXPR(
	userId,
	company,
	role,	
	location,
	startDate,
	endDate)
FROM '/home/csmajs/rsait001/final166/data/work_ex.csv'
WITH DELIMITER ';';

COPY EDUCATIONAL_DETAILS(
	userId,
	instituitionName,
	major,
	degree,
	startDate,
	endDate)
FROM '/home/csmajs/rsait001/final166/data/edu_det.csv'
WITH DELIMITER ';';

COPY CONNECTION_USR(
        userId,
        connectionId,
        status)
FROM '/home/csmajs/rsait001/final166/data/connection.csv'
WITH DELIMITER ';';

COPY MESSAGE(
        msgId,
        senderId,
        receiverId,
        contents, 
        sendTime,
        deleteStatus,
        status)
FROM '/home/csmajs/rsait001/final166/data/msg.csv'
WITH DELIMITER ',';
