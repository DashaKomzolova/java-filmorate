# Filmorate

Социальная сеть для киноманов. Приложение позволяет пользователям:
- Добавлять фильмы и управлять ими
- Ставить лайки фильмам
- Добавлять друг друга в друзья
- Находить популярные фильмы
- Находить общих друзей

---

## Схема базы данных

![Схема БД](docs/db.png)

---

## Описание таблиц

### film
Хранит информацию о фильмах.

| Поле | Тип | Описание |
|------|-----|----------|
| id | BIGINT | Уникальный идентификатор (PRIMARY KEY) |
| name | VARCHAR(255) | Название фильма |
| description | VARCHAR(200) | Описание фильма |
| release_date | DATE | Дата релиза |
| duration | INT | Продолжительность в минутах |
| mpa_id | BIGINT | ID рейтинга MPA (FOREIGN KEY → mpa.id) |

### mpa (Motion Picture Association)
Справочник возрастных рейтингов.

| Поле | Тип | Описание |
|------|-----|----------|
| id | BIGINT | Уникальный идентификатор (PRIMARY KEY) |
| name | VARCHAR(10) | Название рейтинга (G, PG, PG-13, R, NC-17) |

### genre
Справочник жанров фильмов.

| Поле | Тип | Описание |
|------|-----|----------|
| id | BIGINT | Уникальный идентификатор (PRIMARY KEY) |
| name | VARCHAR(50) | Название жанра |

### movie_genre
Связующая таблица для связи фильмов и жанров (многие-ко-многим).

| Поле | Тип | Описание |
|------|-----|----------|
| film_id | BIGINT | ID фильма (PRIMARY KEY, FOREIGN KEY → film.id) |
| genre_id | BIGINT | ID жанра (PRIMARY KEY, FOREIGN KEY → genre.id) |

### user
Хранит информацию о пользователях.

| Поле | Тип | Описание |
|------|-----|----------|
| id | BIGINT | Уникальный идентификатор (PRIMARY KEY) |
| email | VARCHAR(255) | Email пользователя |
| login | VARCHAR(255) | Логин пользователя |
| name | VARCHAR(255) | Имя пользователя |
| birthday | DATE | Дата рождения |

### likes
Хранит лайки пользователей на фильмы.

| Поле | Тип | Описание |
|------|-----|----------|
| film_id | BIGINT | ID фильма (PRIMARY KEY, FOREIGN KEY → film.id) |
| user_id | BIGINT | ID пользователя (PRIMARY KEY, FOREIGN KEY → user.id) |

### friends
Хранит связи дружбы между пользователями.

| Поле | Тип | Описание |
|------|-----|----------|
| user_id | BIGINT | ID пользователя (PRIMARY KEY, FOREIGN KEY → user.id) |
| friend_id | BIGINT | ID друга (PRIMARY KEY, FOREIGN KEY → user.id) |
| status | VARCHAR(20) | Статус дружбы (PENDING, CONFIRMED) |

---

### Описание связей:
- **film → mpa**: Многие фильмы могут иметь один рейтинг (многие-к-одному), связь через `film.mpa_id → mpa.id`
- **film → genre**: Многие фильмы могут иметь много жанров (многие-ко-многим) через таблицу `movie_genre` (`movie_genre.film_id → film.id`, `movie_genre.genre_id → genre.id`)
- **film → user**: Многие пользователи могут лайкать многие фильмы (многие-ко-многим) через таблицу `likes` (`likes.film_id → film.id`, `likes.user_id → user.id`)
- **user → user**: Многие пользователи могут дружить с многими пользователями (многие-ко-многим) через таблицу `friends` (`friends.user_id → user.id`, `friends.friend_id → user.id`)

---

## Примеры SQL-запросов

### Фильмы

#### Получить все фильмы
```sql
SELECT id, name, description, release_date, duration, mpa_id
FROM film;
```

#### Получить фильм по ID
```sql
SELECT id, name, description, release_date, duration, mpa_id
FROM film
WHERE id = ?;
```

#### Получить все жанры фильма
```sql
SELECT g.id, g.name
FROM genre g
JOIN movie_genre mg ON mg.genre_id = g.id
WHERE mg.film_id = ?;
```

#### Получить рейтинг фильма (MPA)
```sql
SELECT m.id, m.name AS rating
FROM mpa m
JOIN film f ON f.mpa_id = m.id
WHERE f.id = ?;
```

#### Получить топ-10 популярных фильмов по лайкам
```sql
SELECT f.id, f.name, COUNT(l.user_id) AS likes_count
FROM film f
LEFT JOIN likes l ON l.film_id = f.id
GROUP BY f.id, f.name
ORDER BY likes_count DESC
LIMIT 10;
```

#### Получить топ-N популярных фильмов
```sql
SELECT f.id, f.name, COUNT(l.user_id) AS likes_count
FROM film f
LEFT JOIN likes l ON l.film_id = f.id
GROUP BY f.id, f.name
ORDER BY likes_count DESC
LIMIT ?;
```

### Пользователи

#### Получить всех пользователей
```sql
SELECT id, email, login, name, birthday
FROM user;
```

#### Получить пользователя по ID
```sql
SELECT id, email, login, name, birthday
FROM user
WHERE id = ?;
```

### Друзья

#### Получить всех друзей пользователя (подтвержденных)
```sql
SELECT u.id, u.email, u.login, u.name, u.birthday
FROM user u
JOIN friends f ON f.friend_id = u.id
WHERE f.user_id = ? AND f.status = 'CONFIRMED';
```

#### Получить общих друзей двух пользователей
```sql
SELECT u.id, u.email, u.login, u.name, u.birthday
FROM user u
JOIN friends f1 ON f1.friend_id = u.id AND f1.status = 'CONFIRMED'
JOIN friends f2 ON f2.friend_id = u.id AND f2.status = 'CONFIRMED'
WHERE f1.user_id = ? AND f2.user_id = ?;
```

#### Добавить друга (создать заявку)
```sql
INSERT INTO friends (user_id, friend_id, status) 
VALUES (?, ?, 'PENDING');
```

#### Получить всех пользователей, лайкнувших фильм
```sql
SELECT u.id, u.login, u.name
FROM user u
JOIN likes l ON l.user_id = u.id
WHERE l.film_id = ?;
```

#### Получить количество лайков у фильма
```sql
SELECT COUNT(*) AS likes_count
FROM likes
WHERE film_id = ?;
```