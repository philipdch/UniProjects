delete from exercise_progress;
delete from subscriptions;
delete from exercises;
delete from muscles_trained;
delete from program_conditions;
delete from programs;
delete from users;

insert into users(user_type, id, email, username, phone_number, password, salt, isLoggedIn, gender, height, weight, date_of_birth, physical_condition) values ('SimpleUser', 1000, 'araki@gmail.com', 'arrakis', '6985648293', '@8f3tv9vdh8wef!', 'g283vdhu38', true, 'M', 172, 62.5, DATE '1999-5-14', 'NORMAL');
insert into users(user_type, id, email, username, phone_number, password, salt, isLoggedIn, firstname, lastname, profilePic, description) values ('Trainer', 2000, 'philip@gmail.com', 'philip_15', '6969696969', '@8f3tv9vdh8wef!', 'g283vdhu38', true, 'Guido', 'Mista', null, 'I am a trainer');

insert into programs(id, name, goals, frequency, difficulty, ageMin, ageMax, cost, trainer_id) values (3000, 'First program', 'Strengthens core and arms', 5, 'ADVANCED', 15, 90, 5, 2000);
insert into programs(id, name, goals, frequency, difficulty, ageMin, ageMax, cost, trainer_id) values (3001, 'Second program', 'Simple legs workout', 2, 'BEGINNER', 18, 60, 15, 2000);

insert into program_conditions(program_id, aimedAt) values (3000, 'NORMAL');
insert into program_conditions(program_id, aimedAt) values (3001, 'NORMAL');
insert into program_conditions(program_id, aimedAt) values (3001, 'FIT');
insert into program_conditions(program_id, aimedAt) values (3001, 'SKINNY');

insert into exercises(id, name, category, description, repetitions, time_required, difficulty, program_id) values (4000, 'Bicep Curls', 'STRENGTH', 'A simple yet effective biceps exercise', 25, 10, 'BEGINNER', 3000);
insert into muscles_trained(exercise_id, musclesTrained) values (4000, 'BICEPS');

insert into subscriptions values (CAST('2022-5-14 17:30:00.69' AS TIMESTAMP), 1000, 3000);

insert into exercise_progress values (CAST((CURRENT_DATE - 1) AS TIMESTAMP), 25, 62.5, 1000, 4000);
insert into exercise_progress values (CAST(CURRENT_DATE AS TIMESTAMP), 0, 62.5, 1000, 4000);
