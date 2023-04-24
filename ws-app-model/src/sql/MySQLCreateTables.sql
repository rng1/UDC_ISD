-- ----------------------------------------------------------------------------
-- Model
-------------------------------------------------------------------------------


DROP TABLE Reply;
DROP TABLE Event;

-- ----------------------------------------------------------------------------
-- Eventos
-- Un id para el evento, su nombre y descripcion, la fecha y hora de la celebracion
-- la duracion de dicho evento, la fecha en la que se crea, un atributo date_cancel
-- para controlar si el evento esta cancelado (el valor es una fecha) o no (el valor
-- es nulo) y un contador para los asistentes y otro para los ausentes
-------------------------------------------------------------------------------

CREATE TABLE Event (eventId BIGINT NOT NULL AUTO_INCREMENT,
	name VARCHAR(80) COLLATE latin1_bin NOT NULL,
	description VARCHAR(1024) COLLATE latin1_bin NOT NULL,
	celebrationDate DATETIME NOT NULL,
	duration SMALLINT NOT NULL,
	creationDate DATETIME NOT NULL,
	cancelationDate DATETIME,
	attendees SMALLINT NOT NULL,
	absences SMALLINT NOT NULL,
	CONSTRAINT EventPK PRIMARY KEY(eventId)) ENGINE = InnoDB;

-- ----------------------------------------------------------------------------
-- Respuestas
-- Un id para la confirmacion, el id del evento para el que se confirma, el email
-- del usuario que hace la confirmacion, la fecha de la respuesta y la respuesta
-- (1 es que asiste, 0 es que no)
-- -----------------------------------------------------------------------------


CREATE TABLE Reply (replyId BIGINT NOT NULL AUTO_INCREMENT,
	eventId BIGINT NOT NULL,
	userEmail VARCHAR(50) COLLATE latin1_bin NOT NULL,
	replyDate DATETIME NOT NULL,
	replyValue BIT NOT NULL,
	CONSTRAINT ReplyPK PRIMARY KEY(replyId),
	CONSTRAINT ConfirmationEventFK FOREIGN KEY(eventId)
	    REFERENCES Event(eventId) ON DELETE CASCADE) ENGINE = InnoDB;
-- -------------------------------------------------------------------