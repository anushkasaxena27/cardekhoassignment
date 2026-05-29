-- App users (JWT)
CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(40) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE refresh_token (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE car_make (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    country VARCHAR(120),
    logo_url VARCHAR(1024)
);

CREATE TABLE car_model (
    id BIGSERIAL PRIMARY KEY,
    make_id BIGINT NOT NULL REFERENCES car_make (id) ON DELETE CASCADE,
    model_name VARCHAR(200) NOT NULL,
    segment VARCHAR(80),
    body_type VARCHAR(80),
    launch_year INTEGER,
    UNIQUE (make_id, model_name)
);

CREATE TABLE car_variant (
    id BIGSERIAL PRIMARY KEY,
    model_id BIGINT NOT NULL REFERENCES car_model (id) ON DELETE CASCADE,
    variant_name VARCHAR(255) NOT NULL,
    fuel_type VARCHAR(40) NOT NULL,
    transmission VARCHAR(40) NOT NULL,
    seating_capacity INTEGER NOT NULL,
    engine_cc INTEGER,
    horsepower INTEGER,
    torque INTEGER,
    mileage NUMERIC(5, 2),
    city_mileage NUMERIC(5, 2),
    highway_mileage NUMERIC(5, 2),
    boot_space INTEGER,
    ground_clearance INTEGER,
    drivetrain VARCHAR(40),
    ex_showroom_price NUMERIC(14, 2) NOT NULL,
    on_road_price NUMERIC(14, 2),
    image_url VARCHAR(1024),
    availability_status VARCHAR(40) NOT NULL DEFAULT 'AVAILABLE',
    search_count BIGINT NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    UNIQUE (model_id, variant_name)
);

CREATE TABLE safety_rating (
    id BIGSERIAL PRIMARY KEY,
    variant_id BIGINT NOT NULL REFERENCES car_variant (id) ON DELETE CASCADE,
    agency VARCHAR(80) NOT NULL,
    rating NUMERIC(3, 1),
    airbags INTEGER,
    abs BOOLEAN NOT NULL DEFAULT FALSE,
    esc BOOLEAN NOT NULL DEFAULT FALSE,
    adas_features VARCHAR(500)
);

CREATE TABLE car_features (
    id BIGSERIAL PRIMARY KEY,
    variant_id BIGINT NOT NULL UNIQUE REFERENCES car_variant (id) ON DELETE CASCADE,
    sunroof BOOLEAN NOT NULL DEFAULT FALSE,
    ventilated_seats BOOLEAN NOT NULL DEFAULT FALSE,
    connected_car_tech BOOLEAN NOT NULL DEFAULT FALSE,
    infotainment_size NUMERIC(4, 1),
    wireless_charging BOOLEAN NOT NULL DEFAULT FALSE,
    cruise_control BOOLEAN NOT NULL DEFAULT FALSE,
    panoramic_sunroof BOOLEAN NOT NULL DEFAULT FALSE,
    audio_system VARCHAR(200)
);

CREATE TABLE user_review (
    id BIGSERIAL PRIMARY KEY,
    variant_id BIGINT NOT NULL REFERENCES car_variant (id) ON DELETE CASCADE,
    username VARCHAR(120) NOT NULL,
    rating NUMERIC(2, 1) NOT NULL,
    title VARCHAR(255),
    review_text TEXT,
    pros TEXT,
    cons TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE car_image (
    id BIGSERIAL PRIMARY KEY,
    variant_id BIGINT NOT NULL REFERENCES car_variant (id) ON DELETE CASCADE,
    image_url VARCHAR(1024) NOT NULL,
    image_type VARCHAR(40)
);

CREATE TABLE search_log (
    id BIGSERIAL PRIMARY KEY,
    variant_id BIGINT REFERENCES car_variant (id) ON DELETE SET NULL,
    query_text TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE user_shortlist (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    variant_id BIGINT NOT NULL REFERENCES car_variant (id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, variant_id)
);
