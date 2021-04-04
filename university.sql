-- phpMyAdmin SQL Dump
-- version 5.0.4
-- https://www.phpmyadmin.net/
--
-- Хост: 127.0.0.1:3306
-- Время создания: Апр 04 2021 г., 15:23
-- Версия сервера: 8.0.19
-- Версия PHP: 7.1.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `university`
--

-- --------------------------------------------------------

--
-- Структура таблицы `academic_performance`
--

CREATE TABLE `academic_performance` (
  `id_student` int NOT NULL,
  `subject_id` int NOT NULL,
  `score` tinyint NOT NULL,
  `try` set('1','2','3') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Дамп данных таблицы `academic_performance`
--

-- --------------------------------------------------------

--
-- Структура таблицы `curriculum`
--

CREATE TABLE `curriculum` (
  `curriculum_number` int NOT NULL,
  `year_study` year NOT NULL,
  `direction_code` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Дамп данных таблицы `curriculum`
--

-- --------------------------------------------------------

--
-- Структура таблицы `curriculum_subject`
--

CREATE TABLE `curriculum_subject` (
  `subject_id` int NOT NULL,
  `curriculum_number` int NOT NULL,
  `subject_code` varchar(30) NOT NULL,
  `semester` int UNSIGNED NOT NULL,
  `number_of_hours` int UNSIGNED NOT NULL,
  `reporting_form` set('зачет','экзамен','отсутствует') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Дамп данных таблицы `curriculum_subject`
--

-- --------------------------------------------------------

--
-- Структура таблицы `department`
--

CREATE TABLE `department` (
  `department_code` int NOT NULL,
  `department_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Дамп данных таблицы `department`
--

-- --------------------------------------------------------

--
-- Структура таблицы `direction`
--

CREATE TABLE `direction` (
  `direction_code` int NOT NULL,
  `direction_name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Дамп данных таблицы `direction`
--

-- --------------------------------------------------------

--
-- Структура таблицы `discipline`
--

CREATE TABLE `discipline` (
  `subject_code` varchar(30) NOT NULL,
  `subject_name` varchar(50) NOT NULL,
  `department_code` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Дамп данных таблицы `discipline`
--

-- --------------------------------------------------------

--
-- Структура таблицы `group`
--

CREATE TABLE `group` (
  `group_number` varchar(10) NOT NULL,
  `curriculum_id` int NOT NULL,
  `qualification_number` set('бакалавр','магистр','специалитет') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Дамп данных таблицы `group`
--

-- --------------------------------------------------------

--
-- Структура таблицы `student`
--

CREATE TABLE `student` (
  `id_student` int NOT NULL,
  `lastname` varchar(50) NOT NULL,
  `firstname` varchar(50) NOT NULL,
  `middlename` varchar(50) DEFAULT NULL,
  `gender` set('М','Ж') NOT NULL,
  `birthday` date NOT NULL,
  `group` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Дамп данных таблицы `student`
--

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `academic_performance`
--
ALTER TABLE `academic_performance`
  ADD PRIMARY KEY (`id_student`,`subject_id`),
  ADD KEY `academic_performance_ibfk_1` (`subject_id`);

--
-- Индексы таблицы `curriculum`
--
ALTER TABLE `curriculum`
  ADD PRIMARY KEY (`curriculum_number`),
  ADD KEY `curriculum_direction` (`direction_code`);

--
-- Индексы таблицы `curriculum_subject`
--
ALTER TABLE `curriculum_subject`
  ADD PRIMARY KEY (`subject_id`),
  ADD KEY `curriculum_subject_ibfk_1` (`curriculum_number`),
  ADD KEY `curriculum_subject_ibfk_2` (`subject_code`);

--
-- Индексы таблицы `department`
--
ALTER TABLE `department`
  ADD PRIMARY KEY (`department_code`);

--
-- Индексы таблицы `direction`
--
ALTER TABLE `direction`
  ADD PRIMARY KEY (`direction_code`);

--
-- Индексы таблицы `discipline`
--
ALTER TABLE `discipline`
  ADD PRIMARY KEY (`subject_code`),
  ADD KEY `discipline_department` (`department_code`);

--
-- Индексы таблицы `group`
--
ALTER TABLE `group`
  ADD PRIMARY KEY (`group_number`),
  ADD KEY `group_curriculum` (`curriculum_id`);

--
-- Индексы таблицы `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`id_student`),
  ADD KEY `student_group` (`group`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `student`
--
ALTER TABLE `student`
  MODIFY `id_student` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=101;

--
-- Ограничения внешнего ключа сохраненных таблиц
--

--
-- Ограничения внешнего ключа таблицы `academic_performance`
--
ALTER TABLE `academic_performance`
  ADD CONSTRAINT `academic_performance_ibfk_1` FOREIGN KEY (`id_student`) REFERENCES `student` (`id_student`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `academic_performance_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `curriculum_subject` (`subject_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `curriculum`
--
ALTER TABLE `curriculum`
  ADD CONSTRAINT `curriculum_direction` FOREIGN KEY (`direction_code`) REFERENCES `direction` (`direction_code`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `curriculum_subject`
--
ALTER TABLE `curriculum_subject`
  ADD CONSTRAINT `curriculum_subject_ibfk_1` FOREIGN KEY (`curriculum_number`) REFERENCES `curriculum` (`curriculum_number`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `curriculum_subject_ibfk_2` FOREIGN KEY (`subject_code`) REFERENCES `discipline` (`subject_code`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `discipline`
--
ALTER TABLE `discipline`
  ADD CONSTRAINT `discipline_department` FOREIGN KEY (`department_code`) REFERENCES `department` (`department_code`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `group`
--
ALTER TABLE `group`
  ADD CONSTRAINT `group_curriculum` FOREIGN KEY (`curriculum_id`) REFERENCES `curriculum` (`curriculum_number`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Ограничения внешнего ключа таблицы `student`
--
ALTER TABLE `student`
  ADD CONSTRAINT `student_group` FOREIGN KEY (`group`) REFERENCES `group` (`group_number`) ON DELETE RESTRICT ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
