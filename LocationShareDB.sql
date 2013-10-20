SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `Location` ;
USE `Location` ;

-- -----------------------------------------------------
-- Table `Location`.`Messages`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Location`.`Messages` (
  `idMessages` INT NOT NULL AUTO_INCREMENT,
  `FromName` VARCHAR(50) NOT NULL,
  `ToName` VARCHAR(50) NOT NULL,
  `Contents` VARCHAR(1024) NOT NULL,
  `Longitude` FLOAT(10,6) NOT NULL,
  `Latitude` FLOAT(10,6) NOT NULL,
  `New` TINYINT(1) NOT NULL,
  `TimeStamp` DATETIME NOT NULL,
  PRIMARY KEY (`idMessages`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;