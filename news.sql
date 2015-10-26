CREATE DATABASE IF NOT EXISTS sohunews;
USE sohunews;

--
-- Definition of table `news`
--

DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `newsid` int(11) NOT NULL auto_increment,
  `newstitle` varchar(60) NOT NULL,
  `newsauthor` varchar(20) NOT NULL,
  `newscontent` text NOT NULL,
  `newsurl` char(130) NOT NULL,
  `newsdate` varchar(24) NOT NULL,
  PRIMARY KEY  (`newsid`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
