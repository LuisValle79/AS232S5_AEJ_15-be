-- Tabla para almacenar información de YouTube MP3
CREATE TABLE IF NOT EXISTS youtube_mp3 (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    link VARCHAR(255),
    video_id VARCHAR(50),
    quality VARCHAR(20),
    thumbnail VARCHAR(255),
    duration VARCHAR(50),
    search_date VARCHAR(50)
);

-- Tabla para almacenar información de películas
CREATE TABLE IF NOT EXISTS movies (
    id SERIAL PRIMARY KEY,
    movie_id VARCHAR(100),
    title VARCHAR(255),
    overview TEXT,
    poster_path VARCHAR(255),
    release_date VARCHAR(50),
    vote_average DECIMAL(3,1),
    vote_count INTEGER,
    popularity DECIMAL(10,3),
    genre_ids VARCHAR(100),
    search_date VARCHAR(50)
);

-- Tabla para almacenar información de trabajos
CREATE TABLE IF NOT EXISTS jobs (
    id SERIAL PRIMARY KEY,
    job_id VARCHAR(100),
    employer_name VARCHAR(255),
    job_title VARCHAR(255),
    job_description TEXT,
    job_country VARCHAR(100),
    job_city VARCHAR(100),
    job_posted_at VARCHAR(50),
    job_apply_link VARCHAR(255),
    job_employment_type VARCHAR(100),
    search_date VARCHAR(50)
);