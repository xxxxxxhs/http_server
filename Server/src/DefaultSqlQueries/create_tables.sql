CREATE TABLE mpaa_rating (
    id INT PRIMARY KEY DEFAULT nextval('seq_mpaa_rating_id'),
    value VARCHAR(64)
);
CREATE TABLE color (
    id INT PRIMARY KEY DEFAULT nextval('seq_color_id'),
    value VARCHAR(64)
);
CREATE TABLE coordinates (
    id INT PRIMARY KEY DEFAULT nextval('seq_coordinates_id'),
    x FLOAT,
    y DOUBLE PRECISION
);
CREATE TABLE location (
    id INT PRIMARY KEY DEFAULT nextval('seq_location_id'),
    x DOUBLE PRECISION,
    y FLOAT,
    z BIGINT,
    name VARCHAR(64)
);
CREATE TABLE person (
    id INT PRIMARY KEY DEFAULT nextval('seq_person_id'),
    name VARCHAR(64),
    passport_id VARCHAR(64),
    eyecolor_id INT REFERENCES color(id),
    haircolor_id INT REFERENCES color(id),
    location_id INT REFERENCES location(id)
);
CREATE TABLE movie (
    id INT PRIMARY KEY DEFAULT nextval('seq_movie_id'),
    name VARCHAR(64),
    coordinates_id INT REFERENCES coordinates(id),
    creation_date TIMESTAMP DEFAULT NOW(),
    oscars_count INT,
    golden_palm_count BIGINT,
    total_box_office FLOAT,
    mpaa_rating_id INT REFERENCES mpaa_rating(id),
    person_id INT REFERENCES person(id)
);