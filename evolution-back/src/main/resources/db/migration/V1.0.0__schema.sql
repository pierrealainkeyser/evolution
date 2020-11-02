create table game(uuid varchar(36) not null, content text not null, quickplay boolean  not null, traits text, terminated boolean  not null, primary key (uuid));
create table player(player varchar(36) not null, user varchar(255) not null, game varchar(36) not null, index int not null, score int, alpha boolean, primary key(player));
create table user(uid varchar(255) not null, name varchar(255) not null, primary key(uid));

alter table player add constraint fk_player_game foreign key (game) references game on delete cascade;
alter table player add constraint fk_player_user foreign key (user) references user;