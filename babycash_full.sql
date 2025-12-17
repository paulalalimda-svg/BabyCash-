--
-- PostgreSQL database dump
--

\restrict M4M8qhYGhTaMmhDvVCSZsLzaL6BKZQ1BvMifObARZGw9Lk7qxRFe0jg3zlJopCq

-- Dumped from database version 18.0
-- Dumped by pg_dump version 18.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE ONLY public.blog_comments DROP CONSTRAINT fkrt9ek0aspkel4ef2fiv7x6w79;
ALTER TABLE ONLY public.cart_items DROP CONSTRAINT fkpcttvuq4mxppo8sxggjtn5i2c;
ALTER TABLE ONLY public.order_items DROP CONSTRAINT fkocimc7dtr037rh4ls4l95nlfi;
ALTER TABLE ONLY public.loyalty_points DROP CONSTRAINT fklsm6njoij1rr7o56mwgncqe5d;
ALTER TABLE ONLY public.blog_posts DROP CONSTRAINT fklog64k5g2l1679hjl2wuyyk5n;
ALTER TABLE ONLY public.loyalty_points DROP CONSTRAINT fkjx52acv9c5myhf3y1vmrufayb;
ALTER TABLE ONLY public.blog_comments DROP CONSTRAINT fkhfcfe7kposiby2u55l8d39fqr;
ALTER TABLE ONLY public.blog_comments DROP CONSTRAINT fkbovv268mg0vg57pkp0nb1bkq4;
ALTER TABLE ONLY public.order_items DROP CONSTRAINT fkbioxgbv59vetrxe0ejfubep1w;
ALTER TABLE ONLY public.carts DROP CONSTRAINT fkb5o626f86h46m4s7ms6ginnop;
ALTER TABLE ONLY public.wishlist_items DROP CONSTRAINT fk_wishlist_items_wishlist;
ALTER TABLE ONLY public.review_votes DROP CONSTRAINT fk_review_votes_review;
ALTER TABLE ONLY public.refresh_tokens DROP CONSTRAINT fk_refresh_token_user;
ALTER TABLE ONLY public.discount_usages DROP CONSTRAINT fk_discount_usages_discount;
ALTER TABLE ONLY public.blog_post_tags DROP CONSTRAINT fk9lwi4pg2kl7ce7pa3r3yotb9w;
ALTER TABLE ONLY public.payments DROP CONSTRAINT fk81gagumt0r8y3rmudcgpbk42l;
ALTER TABLE ONLY public.orders DROP CONSTRAINT fk32ql8ubntj5uh44ph9659tiih;
ALTER TABLE ONLY public.cart_items DROP CONSTRAINT fk1re40cjegsfvw58xrkdp6bac6;
DROP TRIGGER trg_wishlists_update_timestamp ON public.wishlists;
DROP TRIGGER trg_reviews_update_timestamp ON public.reviews;
DROP TRIGGER trg_review_votes_update_counts ON public.review_votes;
DROP TRIGGER trg_product_variants_update_timestamp ON public.product_variants;
DROP TRIGGER trg_discounts_update_timestamp ON public.discounts;
DROP TRIGGER trg_addresses_update_timestamp ON public.addresses;
DROP INDEX public.idx_wishlists_user_id;
DROP INDEX public.idx_wishlist_items_wishlist_id;
DROP INDEX public.idx_wishlist_items_product_id;
DROP INDEX public.idx_variants_product_id;
DROP INDEX public.idx_variants_active;
DROP INDEX public.idx_reviews_verified_purchase;
DROP INDEX public.idx_reviews_user_id;
DROP INDEX public.idx_reviews_rating;
DROP INDEX public.idx_reviews_product_id;
DROP INDEX public.idx_reviews_helpfulness;
DROP INDEX public.idx_reviews_created_at;
DROP INDEX public.idx_reviews_approved;
DROP INDEX public.idx_review_votes_user_id;
DROP INDEX public.idx_review_votes_review_id;
DROP INDEX public.idx_refresh_user;
DROP INDEX public.idx_refresh_token;
DROP INDEX public.idx_refresh_expiry;
DROP INDEX public.idx_notifications_user_id;
DROP INDEX public.idx_notifications_type;
DROP INDEX public.idx_notifications_read;
DROP INDEX public.idx_notifications_created_at;
DROP INDEX public.idx_discounts_dates;
DROP INDEX public.idx_discounts_code;
DROP INDEX public.idx_discounts_active;
DROP INDEX public.idx_discount_usages_user_id;
DROP INDEX public.idx_discount_usages_order_id;
DROP INDEX public.idx_discount_usages_discount_id;
DROP INDEX public.idx_audit_user;
DROP INDEX public.idx_audit_timestamp;
DROP INDEX public.idx_audit_entity;
DROP INDEX public.idx_audit_action;
DROP INDEX public.idx_addresses_user_id;
DROP INDEX public.idx_addresses_is_default;
ALTER TABLE ONLY public.wishlists DROP CONSTRAINT wishlists_pkey;
ALTER TABLE ONLY public.wishlist_items DROP CONSTRAINT wishlist_items_pkey;
ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
ALTER TABLE ONLY public.wishlist_items DROP CONSTRAINT uq_wishlist_items_unique;
ALTER TABLE ONLY public.reviews DROP CONSTRAINT uq_reviews_user_product;
ALTER TABLE ONLY public.review_votes DROP CONSTRAINT uq_review_votes_user_review;
ALTER TABLE ONLY public.orders DROP CONSTRAINT uknthkiu7pgmnqnu86i2jyoe2v7;
ALTER TABLE ONLY public.payments DROP CONSTRAINT uklryndveuwa4k5qthti0pkmtlx;
ALTER TABLE ONLY public.refresh_tokens DROP CONSTRAINT ukghpmfn23vmxfu3spu3lfg4r2d;
ALTER TABLE ONLY public.blog_posts DROP CONSTRAINT ukfmrqlsu8hgt4xyp3ewt66h287;
ALTER TABLE ONLY public.payments DROP CONSTRAINT uk8vo36cen604as7etdfwmyjsxt;
ALTER TABLE ONLY public.users DROP CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7;
ALTER TABLE ONLY public.carts DROP CONSTRAINT uk64t7ox312pqal3p7fg9o503c2;
ALTER TABLE ONLY public.testimonials DROP CONSTRAINT testimonials_pkey;
ALTER TABLE ONLY public.reviews DROP CONSTRAINT reviews_pkey;
ALTER TABLE ONLY public.review_votes DROP CONSTRAINT review_votes_pkey;
ALTER TABLE ONLY public.refresh_tokens DROP CONSTRAINT refresh_tokens_pkey;
ALTER TABLE ONLY public.products DROP CONSTRAINT products_pkey;
ALTER TABLE ONLY public.product_variants DROP CONSTRAINT product_variants_sku_key;
ALTER TABLE ONLY public.product_variants DROP CONSTRAINT product_variants_pkey;
ALTER TABLE ONLY public.payments DROP CONSTRAINT payments_pkey;
ALTER TABLE ONLY public.orders DROP CONSTRAINT orders_pkey;
ALTER TABLE ONLY public.order_items DROP CONSTRAINT order_items_pkey;
ALTER TABLE ONLY public.notifications DROP CONSTRAINT notifications_pkey;
ALTER TABLE ONLY public.loyalty_points DROP CONSTRAINT loyalty_points_pkey;
ALTER TABLE ONLY public.discounts DROP CONSTRAINT discounts_pkey;
ALTER TABLE ONLY public.discounts DROP CONSTRAINT discounts_code_key;
ALTER TABLE ONLY public.discount_usages DROP CONSTRAINT discount_usages_pkey;
ALTER TABLE ONLY public.contact_messages DROP CONSTRAINT contact_messages_pkey;
ALTER TABLE ONLY public.contact_info DROP CONSTRAINT contact_info_pkey;
ALTER TABLE ONLY public.carts DROP CONSTRAINT carts_pkey;
ALTER TABLE ONLY public.cart_items DROP CONSTRAINT cart_items_pkey;
ALTER TABLE ONLY public.blog_posts DROP CONSTRAINT blog_posts_pkey;
ALTER TABLE ONLY public.blog_comments DROP CONSTRAINT blog_comments_pkey;
ALTER TABLE ONLY public.audit_logs DROP CONSTRAINT audit_logs_pkey;
ALTER TABLE ONLY public.addresses DROP CONSTRAINT addresses_pkey;
ALTER TABLE public.wishlists ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.wishlist_items ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.reviews ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.review_votes ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.product_variants ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.notifications ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.discounts ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.discount_usages ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public.addresses ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE public.wishlists_id_seq;
DROP TABLE public.wishlists;
DROP SEQUENCE public.wishlist_items_id_seq;
DROP TABLE public.wishlist_items;
DROP TABLE public.users;
DROP TABLE public.testimonials;
DROP SEQUENCE public.reviews_id_seq;
DROP TABLE public.reviews;
DROP SEQUENCE public.review_votes_id_seq;
DROP TABLE public.review_votes;
DROP TABLE public.refresh_tokens;
DROP TABLE public.products;
DROP SEQUENCE public.product_variants_id_seq;
DROP TABLE public.product_variants;
DROP TABLE public.payments;
DROP TABLE public.orders;
DROP TABLE public.order_items;
DROP SEQUENCE public.notifications_id_seq;
DROP TABLE public.notifications;
DROP TABLE public.loyalty_points;
DROP SEQUENCE public.discounts_id_seq;
DROP TABLE public.discounts;
DROP SEQUENCE public.discount_usages_id_seq;
DROP TABLE public.discount_usages;
DROP TABLE public.contact_messages;
DROP TABLE public.contact_info;
DROP TABLE public.carts;
DROP TABLE public.cart_items;
DROP TABLE public.blog_posts;
DROP TABLE public.blog_post_tags;
DROP TABLE public.blog_comments;
DROP TABLE public.audit_logs;
DROP SEQUENCE public.addresses_id_seq;
DROP TABLE public.addresses;
DROP FUNCTION public.update_updated_at_column();
DROP FUNCTION public.update_review_votes();
DROP FUNCTION public.reduce_stock_on_order_confirmation();
DROP FUNCTION public.generate_order_number();
DROP TYPE public.user_role;
DROP TYPE public.product_category;
DROP TYPE public.payment_status;
DROP TYPE public.payment_method;
DROP TYPE public.order_status;
DROP EXTENSION "uuid-ossp";
DROP EXTENSION pg_trgm;
--
-- Name: pg_trgm; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;


--
-- Name: EXTENSION pg_trgm; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


--
-- Name: order_status; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.order_status AS ENUM (
    'PENDING',
    'PAID',
    'PROCESSING',
    'SHIPPED',
    'DELIVERED',
    'CANCELLED',
    'REFUNDED'
);


--
-- Name: payment_method; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.payment_method AS ENUM (
    'CREDIT_CARD',
    'DEBIT_CARD',
    'PAYPAL',
    'STRIPE',
    'MERCADO_PAGO',
    'CASH_ON_DELIVERY'
);


--
-- Name: payment_status; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.payment_status AS ENUM (
    'PENDING',
    'PROCESSING',
    'COMPLETED',
    'FAILED',
    'REFUNDED'
);


--
-- Name: product_category; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.product_category AS ENUM (
    'CLOTHING',
    'TOYS',
    'FEEDING',
    'DIAPERS',
    'FURNITURE',
    'BATH',
    'SAFETY',
    'HEALTH',
    'BOOKS',
    'ACCESSORIES'
);


--
-- Name: user_role; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.user_role AS ENUM (
    'USER',
    'ADMIN',
    'MODERATOR'
);


--
-- Name: generate_order_number(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.generate_order_number() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    next_number INTEGER;
    year_part VARCHAR(4);
BEGIN
    year_part := EXTRACT(YEAR FROM CURRENT_TIMESTAMP)::VARCHAR;
    
    SELECT COALESCE(MAX(CAST(SUBSTRING(order_number FROM 10) AS INTEGER)), 0) + 1
    INTO next_number
    FROM orders
    WHERE order_number LIKE 'ORD-' || year_part || '-%';
    
    NEW.order_number := 'ORD-' || year_part || '-' || LPAD(next_number::VARCHAR, 5, '0');
    
    RETURN NEW;
END;
$$;


--
-- Name: reduce_stock_on_order_confirmation(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.reduce_stock_on_order_confirmation() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.status = 'PAID' AND (OLD.status IS NULL OR OLD.status != 'PAID') THEN
        UPDATE products p
        SET stock = stock - oi.quantity
        FROM order_items oi
        WHERE oi.order_id = NEW.id
        AND p.id = oi.product_id
        AND p.track_inventory = TRUE;
    END IF;
    
    RETURN NEW;
END;
$$;


--
-- Name: update_review_votes(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.update_review_votes() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        IF NEW.vote_type = 'UP' THEN
            UPDATE reviews SET upvotes = upvotes + 1 WHERE id = NEW.review_id;
        ELSE
            UPDATE reviews SET downvotes = downvotes + 1 WHERE id = NEW.review_id;
        END IF;
    ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.vote_type = 'UP' AND NEW.vote_type = 'DOWN' THEN
            UPDATE reviews SET upvotes = upvotes - 1, downvotes = downvotes + 1 WHERE id = NEW.review_id;
        ELSIF OLD.vote_type = 'DOWN' AND NEW.vote_type = 'UP' THEN
            UPDATE reviews SET upvotes = upvotes + 1, downvotes = downvotes - 1 WHERE id = NEW.review_id;
        END IF;
    ELSIF TG_OP = 'DELETE' THEN
        IF OLD.vote_type = 'UP' THEN
            UPDATE reviews SET upvotes = upvotes - 1 WHERE id = OLD.review_id;
        ELSE
            UPDATE reviews SET downvotes = downvotes - 1 WHERE id = OLD.review_id;
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$;


--
-- Name: update_updated_at_column(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.update_updated_at_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: addresses; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.addresses (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    label character varying(50),
    recipient_name character varying(200) NOT NULL,
    phone character varying(20) NOT NULL,
    street_address character varying(255) NOT NULL,
    apartment_suite character varying(100),
    city character varying(100) NOT NULL,
    state character varying(100) NOT NULL,
    postal_code character varying(20) NOT NULL,
    country character varying(100) DEFAULT 'Colombia'::character varying NOT NULL,
    is_default boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_addresses_phone_format CHECK (((phone)::text ~* '^\+?[0-9]{10,15}$'::text))
);


--
-- Name: addresses_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.addresses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: addresses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.addresses_id_seq OWNED BY public.addresses.id;


--
-- Name: audit_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.audit_logs (
    id bigint NOT NULL,
    action_type character varying(50) NOT NULL,
    description text,
    entity_id bigint,
    entity_type character varying(50),
    error_message text,
    ip_address character varying(45),
    metadata text,
    status character varying(20) NOT NULL,
    "timestamp" timestamp(6) without time zone NOT NULL,
    user_agent text,
    user_id bigint,
    username character varying(100),
    CONSTRAINT audit_logs_action_type_check CHECK (((action_type)::text = ANY (ARRAY[('LOGIN'::character varying)::text, ('LOGOUT'::character varying)::text, ('LOGIN_FAILED'::character varying)::text, ('REGISTER'::character varying)::text, ('ORDER_CREATED'::character varying)::text, ('ORDER_CANCELLED'::character varying)::text, ('ORDER_STATUS_CHANGED'::character varying)::text, ('PAYMENT_INITIATED'::character varying)::text, ('PAYMENT_COMPLETED'::character varying)::text, ('PAYMENT_FAILED'::character varying)::text, ('PAYMENT_REFUNDED'::character varying)::text, ('PRODUCT_CREATED'::character varying)::text, ('PRODUCT_UPDATED'::character varying)::text, ('PRODUCT_DELETED'::character varying)::text, ('PRODUCT_STOCK_CHANGED'::character varying)::text, ('USER_CREATED'::character varying)::text, ('USER_UPDATED'::character varying)::text, ('USER_DELETED'::character varying)::text, ('PASSWORD_CHANGED'::character varying)::text, ('ADMIN_ACTION'::character varying)::text, ('SECURITY_EVENT'::character varying)::text, ('DATA_EXPORT'::character varying)::text, ('SYSTEM_ERROR'::character varying)::text, ('RATE_LIMIT_EXCEEDED'::character varying)::text, ('UNAUTHORIZED_ACCESS'::character varying)::text]))),
    CONSTRAINT audit_logs_status_check CHECK (((status)::text = ANY (ARRAY[('SUCCESS'::character varying)::text, ('FAILURE'::character varying)::text, ('WARNING'::character varying)::text])))
);


--
-- Name: audit_logs_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.audit_logs ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.audit_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: blog_comments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.blog_comments (
    id bigint NOT NULL,
    approved boolean NOT NULL,
    content text NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone,
    blog_post_id bigint NOT NULL,
    parent_comment_id bigint,
    user_id bigint NOT NULL
);


--
-- Name: blog_comments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.blog_comments ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.blog_comments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: blog_post_tags; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.blog_post_tags (
    blog_post_id bigint NOT NULL,
    tag character varying(255)
);


--
-- Name: blog_posts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.blog_posts (
    id bigint NOT NULL,
    content text NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    excerpt text,
    featured boolean NOT NULL,
    image_url character varying(500),
    published boolean NOT NULL,
    published_at timestamp(6) without time zone,
    slug character varying(250) NOT NULL,
    title character varying(200) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    view_count bigint NOT NULL,
    author_id bigint NOT NULL
);


--
-- Name: blog_posts_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.blog_posts ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.blog_posts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: cart_items; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cart_items (
    id bigint NOT NULL,
    added_at timestamp(6) without time zone NOT NULL,
    quantity integer NOT NULL,
    cart_id bigint NOT NULL,
    product_id bigint NOT NULL
);


--
-- Name: cart_items_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.cart_items ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.cart_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: carts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.carts (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    user_id bigint NOT NULL
);


--
-- Name: carts_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.carts ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.carts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: contact_info; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.contact_info (
    id bigint NOT NULL,
    address text NOT NULL,
    business_hours character varying(100),
    business_hours_details text,
    city character varying(100),
    company_name character varying(100) NOT NULL,
    country character varying(100),
    created_at timestamp(6) without time zone NOT NULL,
    description text,
    email character varying(100) NOT NULL,
    facebook character varying(200),
    instagram character varying(200),
    latitude double precision,
    longitude double precision,
    phone character varying(20) NOT NULL,
    twitter character varying(200),
    updated_at timestamp(6) without time zone,
    whatsapp character varying(200)
);


--
-- Name: contact_info_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.contact_info ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.contact_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: contact_messages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.contact_messages (
    id bigint NOT NULL,
    admin_notes character varying(500),
    created_at timestamp(6) without time zone NOT NULL,
    email character varying(100) NOT NULL,
    ip_address character varying(45),
    message character varying(1000) NOT NULL,
    name character varying(100) NOT NULL,
    phone character varying(20),
    read_at timestamp(6) without time zone,
    replied_at timestamp(6) without time zone,
    status character varying(20) NOT NULL,
    subject character varying(150) NOT NULL,
    updated_at timestamp(6) without time zone,
    user_agent character varying(255),
    CONSTRAINT contact_messages_status_check CHECK (((status)::text = ANY (ARRAY[('NEW'::character varying)::text, ('READ'::character varying)::text, ('REPLIED'::character varying)::text, ('ARCHIVED'::character varying)::text])))
);


--
-- Name: contact_messages_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.contact_messages ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.contact_messages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: discount_usages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.discount_usages (
    id bigint NOT NULL,
    discount_id bigint NOT NULL,
    user_id bigint NOT NULL,
    order_id bigint NOT NULL,
    used_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: discount_usages_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.discount_usages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: discount_usages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.discount_usages_id_seq OWNED BY public.discount_usages.id;


--
-- Name: discounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.discounts (
    id bigint NOT NULL,
    code character varying(50) NOT NULL,
    description text,
    type character varying(20) NOT NULL,
    value numeric(10,2) NOT NULL,
    minimum_purchase_amount numeric(10,2),
    maximum_discount_amount numeric(10,2),
    usage_limit integer,
    usage_limit_per_user integer DEFAULT 1,
    applicable_to character varying(20) DEFAULT 'ALL'::character varying NOT NULL,
    applicable_categories public.product_category[],
    applicable_product_ids bigint[],
    starts_at timestamp without time zone NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    active boolean DEFAULT true NOT NULL,
    times_used integer DEFAULT 0 NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_discounts_dates CHECK ((expires_at > starts_at)),
    CONSTRAINT ck_discounts_percentage_range CHECK ((((type)::text <> 'PERCENTAGE'::text) OR (value <= (100)::numeric))),
    CONSTRAINT ck_discounts_type CHECK (((type)::text = ANY (ARRAY[('PERCENTAGE'::character varying)::text, ('FIXED_AMOUNT'::character varying)::text]))),
    CONSTRAINT ck_discounts_value_positive CHECK ((value > (0)::numeric))
);


--
-- Name: discounts_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.discounts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: discounts_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.discounts_id_seq OWNED BY public.discounts.id;


--
-- Name: loyalty_points; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.loyalty_points (
    id bigint NOT NULL,
    active boolean NOT NULL,
    amount_spent numeric(10,2),
    created_at timestamp(6) without time zone NOT NULL,
    description character varying(500),
    expires_at timestamp(6) without time zone,
    points integer NOT NULL,
    transaction_type character varying(20) NOT NULL,
    order_id bigint,
    user_id bigint NOT NULL,
    CONSTRAINT loyalty_points_transaction_type_check CHECK (((transaction_type)::text = ANY (ARRAY[('EARNED'::character varying)::text, ('REDEEMED'::character varying)::text, ('EXPIRED'::character varying)::text, ('BONUS'::character varying)::text, ('REFUND'::character varying)::text])))
);


--
-- Name: loyalty_points_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.loyalty_points ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.loyalty_points_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: notifications; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notifications (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    message text NOT NULL,
    type character varying(50) NOT NULL,
    metadata jsonb,
    reference_type character varying(50),
    reference_id bigint,
    read boolean DEFAULT false NOT NULL,
    read_at timestamp without time zone,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.notifications_id_seq OWNED BY public.notifications.id;


--
-- Name: order_items; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.order_items (
    id bigint NOT NULL,
    quantity integer NOT NULL,
    subtotal numeric(10,2) NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    order_id bigint NOT NULL,
    product_id bigint NOT NULL
);


--
-- Name: order_items_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.order_items ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.order_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: orders; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.orders (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    notes text,
    order_number character varying(50),
    shipping_address text,
    status character varying(20) NOT NULL,
    total_amount numeric(10,2) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT orders_status_check CHECK (((status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('PROCESSING'::character varying)::text, ('SHIPPED'::character varying)::text, ('DELIVERED'::character varying)::text, ('CANCELLED'::character varying)::text])))
);


--
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.orders ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: payments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.payments (
    id bigint NOT NULL,
    amount numeric(10,2) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    metadata text,
    method character varying(20) NOT NULL,
    status character varying(20) NOT NULL,
    transaction_id character varying(255),
    updated_at timestamp(6) without time zone NOT NULL,
    order_id bigint NOT NULL,
    CONSTRAINT payments_method_check CHECK (((method)::text = ANY (ARRAY[('CREDIT_CARD'::character varying)::text, ('DEBIT_CARD'::character varying)::text, ('PAYPAL'::character varying)::text, ('STRIPE'::character varying)::text, ('MERCADOPAGO'::character varying)::text]))),
    CONSTRAINT payments_status_check CHECK (((status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('PROCESSING'::character varying)::text, ('COMPLETED'::character varying)::text, ('FAILED'::character varying)::text, ('REFUNDED'::character varying)::text])))
);


--
-- Name: payments_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.payments ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.payments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: product_variants; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.product_variants (
    id bigint NOT NULL,
    product_id bigint NOT NULL,
    name character varying(100) NOT NULL,
    sku character varying(100),
    size character varying(50),
    color character varying(50),
    material character varying(100),
    price_adjustment numeric(10,2) DEFAULT 0,
    stock integer DEFAULT 0 NOT NULL,
    active boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_variants_stock_non_negative CHECK ((stock >= 0))
);


--
-- Name: product_variants_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.product_variants_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: product_variants_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.product_variants_id_seq OWNED BY public.product_variants.id;


--
-- Name: products; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.products (
    id bigint NOT NULL,
    category character varying(50) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    description text,
    discount_price numeric(10,2),
    enabled boolean NOT NULL,
    featured boolean NOT NULL,
    image_url character varying(500),
    name character varying(255) NOT NULL,
    price numeric(10,2) NOT NULL,
    rating numeric(3,2),
    review_count integer NOT NULL,
    stock integer NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    CONSTRAINT products_category_check CHECK (((category)::text = ANY (ARRAY[('CLOTHING'::character varying)::text, ('TOYS'::character varying)::text, ('FOOD'::character varying)::text, ('FURNITURE'::character varying)::text, ('ACCESSORIES'::character varying)::text, ('HEALTHCARE'::character varying)::text, ('BOOKS'::character varying)::text, ('OTHER'::character varying)::text])))
);


--
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.products ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: refresh_tokens; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.refresh_tokens (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    expiry_date timestamp(6) without time zone NOT NULL,
    ip_address character varying(45),
    revoked boolean NOT NULL,
    revoked_at timestamp(6) without time zone,
    token character varying(512) NOT NULL,
    user_agent character varying(500),
    user_id bigint NOT NULL
);


--
-- Name: refresh_tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.refresh_tokens ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.refresh_tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: review_votes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.review_votes (
    id bigint NOT NULL,
    review_id bigint NOT NULL,
    user_id bigint NOT NULL,
    vote_type character varying(10) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_review_votes_type CHECK (((vote_type)::text = ANY (ARRAY[('UP'::character varying)::text, ('DOWN'::character varying)::text])))
);


--
-- Name: review_votes_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.review_votes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: review_votes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.review_votes_id_seq OWNED BY public.review_votes.id;


--
-- Name: reviews; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reviews (
    id bigint NOT NULL,
    product_id bigint NOT NULL,
    user_id bigint NOT NULL,
    order_id bigint,
    rating integer NOT NULL,
    title character varying(200),
    comment text NOT NULL,
    image_urls text[],
    verified_purchase boolean DEFAULT false NOT NULL,
    upvotes integer DEFAULT 0 NOT NULL,
    downvotes integer DEFAULT 0 NOT NULL,
    approved boolean DEFAULT false NOT NULL,
    flagged boolean DEFAULT false NOT NULL,
    admin_response text,
    admin_response_at timestamp without time zone,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_reviews_rating_range CHECK (((rating >= 1) AND (rating <= 5))),
    CONSTRAINT ck_reviews_votes_non_negative CHECK (((upvotes >= 0) AND (downvotes >= 0)))
);


--
-- Name: TABLE reviews; Type: COMMENT; Schema: public; Owner: -
--

COMMENT ON TABLE public.reviews IS 'Reseñas y calificaciones de productos';


--
-- Name: reviews_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.reviews_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: reviews_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.reviews_id_seq OWNED BY public.reviews.id;


--
-- Name: testimonials; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.testimonials (
    id bigint NOT NULL,
    approved boolean NOT NULL,
    avatar character varying(500),
    created_at timestamp(6) without time zone NOT NULL,
    featured boolean NOT NULL,
    location character varying(100),
    message text NOT NULL,
    name character varying(100) NOT NULL,
    rating integer NOT NULL,
    updated_at timestamp(6) without time zone
);


--
-- Name: testimonials_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.testimonials ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.testimonials_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    email character varying(255) NOT NULL,
    email_verified boolean NOT NULL,
    enabled boolean NOT NULL,
    first_name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    phone character varying(20),
    refresh_token text,
    reset_password_token character varying(255),
    role character varying(20) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    verification_token character varying(255),
    address character varying(500),
    reset_password_expiry timestamp(6) without time zone,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY (ARRAY[('USER'::character varying)::text, ('ADMIN'::character varying)::text])))
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.users ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: wishlist_items; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.wishlist_items (
    id bigint NOT NULL,
    wishlist_id bigint NOT NULL,
    product_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: wishlist_items_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.wishlist_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wishlist_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.wishlist_items_id_seq OWNED BY public.wishlist_items.id;


--
-- Name: wishlists; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.wishlists (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    name character varying(100) DEFAULT 'Mi Lista de Deseos'::character varying NOT NULL,
    description text,
    is_public boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: wishlists_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.wishlists_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wishlists_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.wishlists_id_seq OWNED BY public.wishlists.id;


--
-- Name: addresses id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.addresses ALTER COLUMN id SET DEFAULT nextval('public.addresses_id_seq'::regclass);


--
-- Name: discount_usages id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.discount_usages ALTER COLUMN id SET DEFAULT nextval('public.discount_usages_id_seq'::regclass);


--
-- Name: discounts id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.discounts ALTER COLUMN id SET DEFAULT nextval('public.discounts_id_seq'::regclass);


--
-- Name: notifications id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications ALTER COLUMN id SET DEFAULT nextval('public.notifications_id_seq'::regclass);


--
-- Name: product_variants id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_variants ALTER COLUMN id SET DEFAULT nextval('public.product_variants_id_seq'::regclass);


--
-- Name: review_votes id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.review_votes ALTER COLUMN id SET DEFAULT nextval('public.review_votes_id_seq'::regclass);


--
-- Name: reviews id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reviews ALTER COLUMN id SET DEFAULT nextval('public.reviews_id_seq'::regclass);


--
-- Name: wishlist_items id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.wishlist_items ALTER COLUMN id SET DEFAULT nextval('public.wishlist_items_id_seq'::regclass);


--
-- Name: wishlists id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.wishlists ALTER COLUMN id SET DEFAULT nextval('public.wishlists_id_seq'::regclass);


--
-- Data for Name: addresses; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.addresses (id, user_id, label, recipient_name, phone, street_address, apartment_suite, city, state, postal_code, country, is_default, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: audit_logs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.audit_logs (id, action_type, description, entity_id, entity_type, error_message, ip_address, metadata, status, "timestamp", user_agent, user_id, username) FROM stdin;
1	LOGIN	Refresh token creado para usuario: admin@babycash.com	1	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 10:51:04.283283	\N	\N	\N
2	LOGIN	Refresh token creado para usuario: admin@babycash.com	2	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 12:29:02.385002	\N	\N	\N
3	LOGIN	Refresh token creado para usuario: admin@babycash.com	3	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 12:42:12.131477	\N	\N	\N
4	LOGOUT	Token revocado para usuario: admin@babycash.com	2	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 12:44:59.372484	\N	\N	\N
5	LOGIN	Refresh token creado para usuario: admin@babycash.com	4	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 13:10:34.285057	\N	\N	\N
6	LOGIN	Refresh token creado para usuario: admin@babycash.com	5	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 13:18:28.652858	\N	\N	\N
7	LOGIN	Refresh token creado para usuario: admin@babycash.com	6	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 13:25:11.290663	\N	\N	\N
8	LOGIN	Refresh token creado para usuario: admin@babycash.com	7	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 14:16:13.749665	\N	\N	\N
9	LOGOUT	Token revocado para usuario: admin@babycash.com	7	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 14:16:40.838099	\N	\N	\N
10	LOGIN	Refresh token creado para usuario: demo@babycash.com	8	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 14:52:59.450061	\N	\N	\N
11	LOGIN	Refresh token creado para usuario: demo@babycash.com	9	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 14:54:15.285042	\N	\N	\N
12	LOGIN	Refresh token creado para usuario: admin@babycash.com	10	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 14:59:22.189701	\N	\N	\N
13	LOGIN	Refresh token creado para usuario: demo@babycash.com	11	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 15:10:49.089577	\N	\N	\N
14	LOGOUT	Token revocado para usuario: admin@babycash.com	10	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 15:15:43.454882	\N	\N	\N
15	LOGIN	Refresh token creado para usuario: admin@babycash.com	12	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 15:15:53.077851	\N	\N	\N
16	LOGIN	Refresh token creado para usuario: demo@babycash.com	13	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 15:19:21.006876	\N	\N	\N
17	LOGIN	Refresh token creado para usuario: demo@babycash.com	14	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 15:28:22.075	\N	\N	\N
18	ORDER_CREATED	Nueva orden creada	1	Order	\N	\N	\N	SUCCESS	2025-10-28 15:29:10.172825	\N	\N	\N
19	LOGIN	Refresh token creado para usuario: admin@babycash.com	15	RefreshToken	\N	\N	\N	SUCCESS	2025-10-28 21:48:16.957628	\N	\N	\N
20	LOGIN	Refresh token creado para usuario: admin@babycash.com	1	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 10:10:31.748235	\N	\N	\N
21	LOGIN	Refresh token creado para usuario: admin@babycash.com	2	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 10:10:47.981392	\N	\N	\N
22	LOGIN	Refresh token creado para usuario: demo@babycash.com	3	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 10:11:56.98979	\N	\N	\N
23	LOGOUT	Token revocado para usuario: demo@babycash.com	3	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 10:12:14.348193	\N	\N	\N
24	LOGIN	Refresh token creado para usuario: admin@babycash.co	4	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 10:15:06.298648	\N	\N	\N
25	LOGOUT	Token revocado para usuario: admin@babycash.co	4	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 10:15:35.042932	\N	\N	\N
26	LOGIN	Refresh token creado para usuario: admin@babycash.com	5	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 10:15:46.246566	\N	\N	\N
27	LOGOUT	Token revocado para usuario: admin@babycash.com	5	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 12:38:15.053137	\N	\N	\N
28	LOGIN	Refresh token creado para usuario: admin@babycash.com	6	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 12:42:24.856616	\N	\N	\N
29	LOGOUT	Token revocado para usuario: admin@babycash.com	6	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 13:12:01.44073	\N	\N	\N
30	LOGIN	Refresh token creado para usuario: estefa@gmail.com	7	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 13:12:51.433937	\N	\N	\N
31	LOGIN	Refresh token creado para usuario: admin@babycash.com	8	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 15:42:43.065995	\N	\N	\N
32	LOGIN	Refresh token creado para usuario: admin@babycash.com	9	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 15:56:34.67989	\N	\N	\N
33	LOGIN	Refresh token creado para usuario: demo@babycash.com	10	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 16:03:02.884676	\N	\N	\N
34	LOGIN	Refresh token creado para usuario: estefa@gmail.com	11	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 16:04:35.221206	\N	\N	\N
35	LOGIN	Refresh token creado para usuario: estefa@gmail.com	12	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 16:04:45.340684	\N	\N	\N
36	LOGIN	Refresh token creado para usuario: demo@babycash.com	13	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 16:05:05.483641	\N	\N	\N
37	LOGOUT	Token revocado para usuario: demo@babycash.com	13	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 16:11:45.09688	\N	\N	\N
38	LOGIN	Refresh token creado para usuario: admin@babycash.com	14	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 16:12:02.74002	\N	\N	\N
39	LOGIN	Refresh token creado para usuario: demo@babycash.com	15	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:15:25.809894	\N	\N	\N
40	LOGOUT	Token revocado para usuario: demo@babycash.com	15	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:15:50.937938	\N	\N	\N
41	LOGIN	Refresh token creado para usuario: demo@babycash.com	16	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:16:00.941841	\N	\N	\N
42	LOGOUT	Token revocado para usuario: demo@babycash.com	16	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:16:22.447159	\N	\N	\N
43	LOGIN	Refresh token creado para usuario: admin@babycash.com	17	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:16:35.056946	\N	\N	\N
44	LOGIN	Refresh token creado para usuario: admin@babycash.com	18	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:23:14.84469	\N	\N	\N
45	PRODUCT_CREATED	Producto creado	67	Product	\N	\N	\N	SUCCESS	2025-10-29 19:23:26.418364	\N	\N	\N
46	PRODUCT_UPDATED	Producto actualizado	67	Product	\N	\N	\N	SUCCESS	2025-10-29 19:23:36.459018	\N	\N	\N
47	LOGIN	Refresh token creado para usuario: admin@babycash.com	19	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:44:20.071091	\N	\N	\N
48	LOGIN	Refresh token creado para usuario: admin@babycash.com	20	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:44:32.552504	\N	\N	\N
49	LOGIN	Refresh token creado para usuario: admin@babycash.com	21	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:47:37.531702	\N	\N	\N
50	PRODUCT_CREATED	Producto creado	68	Product	\N	\N	\N	SUCCESS	2025-10-29 19:47:37.630802	\N	\N	\N
51	LOGOUT	Token revocado para usuario: admin@babycash.com	17	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:53:33.954365	\N	\N	\N
52	LOGIN	Refresh token creado para usuario: demo@babycash.com	22	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:55:28.525316	\N	\N	\N
53	LOGOUT	Token revocado para usuario: demo@babycash.com	22	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:56:05.541201	\N	\N	\N
54	LOGIN	Refresh token creado para usuario: admin@babycash.com	23	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:56:18.417935	\N	\N	\N
55	LOGOUT	Token revocado para usuario: admin@babycash.com	23	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 19:57:46.962285	\N	\N	\N
56	LOGIN	Refresh token creado para usuario: demo@babycash.com	24	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 20:03:56.538356	\N	\N	\N
57	LOGOUT	Token revocado para usuario: demo@babycash.com	24	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 20:04:58.636298	\N	\N	\N
58	LOGIN	Refresh token creado para usuario: admin@babycash.com	25	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 20:05:06.055069	\N	\N	\N
59	LOGOUT	Token revocado para usuario: admin@babycash.com	25	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 20:26:36.96735	\N	\N	\N
60	LOGIN	Refresh token creado para usuario: admin@babycash.com	26	RefreshToken	\N	\N	\N	SUCCESS	2025-10-29 20:27:02.672807	\N	\N	\N
61	PRODUCT_UPDATED	Producto actualizado	68	Product	\N	\N	\N	SUCCESS	2025-10-29 20:28:27.58312	\N	\N	\N
62	PRODUCT_DELETED	Producto eliminado	68	Product	\N	\N	\N	SUCCESS	2025-10-29 20:28:32.097749	\N	\N	\N
63	PRODUCT_DELETED	Producto eliminado	67	Product	\N	\N	\N	SUCCESS	2025-10-29 20:28:53.939227	\N	\N	\N
64	PRODUCT_DELETED	Producto eliminado	55	Product	\N	\N	\N	SUCCESS	2025-10-29 20:29:07.269005	\N	\N	\N
65	PRODUCT_DELETED	Producto eliminado	64	Product	\N	\N	\N	SUCCESS	2025-10-29 20:29:14.104716	\N	\N	\N
66	PRODUCT_DELETED	Producto eliminado	53	Product	\N	\N	\N	SUCCESS	2025-10-29 20:29:18.568443	\N	\N	\N
67	PRODUCT_DELETED	Producto eliminado	61	Product	\N	\N	\N	SUCCESS	2025-10-29 20:29:27.269626	\N	\N	\N
68	PRODUCT_DELETED	Producto eliminado	58	Product	\N	\N	\N	SUCCESS	2025-10-29 20:29:34.071725	\N	\N	\N
69	PRODUCT_DELETED	Producto eliminado	50	Product	\N	\N	\N	SUCCESS	2025-10-29 20:29:51.203617	\N	\N	\N
70	PRODUCT_DELETED	Producto eliminado	66	Product	\N	\N	\N	SUCCESS	2025-10-29 20:29:54.582976	\N	\N	\N
71	PRODUCT_DELETED	Producto eliminado	65	Product	\N	\N	\N	SUCCESS	2025-10-29 20:29:56.79461	\N	\N	\N
72	PRODUCT_DELETED	Producto eliminado	51	Product	\N	\N	\N	SUCCESS	2025-10-29 20:30:17.584256	\N	\N	\N
73	PRODUCT_UPDATED	Producto actualizado	63	Product	\N	\N	\N	SUCCESS	2025-10-29 20:49:36.966367	\N	\N	\N
74	PRODUCT_DELETED	Producto eliminado	62	Product	\N	\N	\N	SUCCESS	2025-10-29 20:49:46.349857	\N	\N	\N
75	PRODUCT_CREATED	Producto creado	69	Product	\N	\N	\N	SUCCESS	2025-10-29 20:58:37.652372	\N	\N	\N
76	PRODUCT_CREATED	Producto creado	70	Product	\N	\N	\N	SUCCESS	2025-10-29 20:59:06.184724	\N	\N	\N
77	LOGOUT	Token revocado para usuario: admin@babycash.com	26	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 06:55:32.273085	\N	\N	\N
78	LOGIN	Refresh token creado para usuario: demo@babycash.com	27	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 06:55:42.430266	\N	\N	\N
79	LOGOUT	Token revocado para usuario: demo@babycash.com	27	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 06:58:43.546428	\N	\N	\N
80	LOGIN	Refresh token creado para usuario: admin@babycash.com	28	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 06:58:59.271027	\N	\N	\N
81	PRODUCT_CREATED	Producto creado	71	Product	\N	\N	\N	SUCCESS	2025-10-30 12:21:48.350195	\N	\N	\N
82	PRODUCT_CREATED	Producto creado	72	Product	\N	\N	\N	SUCCESS	2025-10-30 12:23:01.885091	\N	\N	\N
83	PRODUCT_UPDATED	Producto actualizado	72	Product	\N	\N	\N	SUCCESS	2025-10-30 12:24:05.192658	\N	\N	\N
84	ORDER_CREATED	Nueva orden creada	6	Order	\N	\N	\N	SUCCESS	2025-10-30 12:38:05.810091	\N	\N	\N
85	ORDER_CREATED	Nueva orden creada	7	Order	\N	\N	\N	SUCCESS	2025-10-30 12:47:04.764547	\N	\N	\N
86	ORDER_CREATED	Nueva orden creada	8	Order	\N	\N	\N	SUCCESS	2025-10-30 12:55:45.428909	\N	\N	\N
87	ORDER_CREATED	Nueva orden creada	9	Order	\N	\N	\N	SUCCESS	2025-10-30 13:15:57.704839	\N	\N	\N
88	LOGIN	Refresh token creado para usuario: demo@babycash.com	29	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 16:07:56.413932	\N	\N	\N
89	LOGOUT	Token revocado para usuario: demo@babycash.com	29	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 16:08:38.08729	\N	\N	\N
90	LOGIN	Refresh token creado para usuario: admin@babycash.com	30	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 16:08:48.642876	\N	\N	\N
91	ORDER_CREATED	Nueva orden creada	10	Order	\N	\N	\N	SUCCESS	2025-10-30 16:14:01.618808	\N	\N	\N
92	LOGIN	Refresh token creado para usuario: admin@babycash.com	31	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 16:28:09.923469	\N	\N	\N
93	ORDER_CREATED	Nueva orden creada	11	Order	\N	\N	\N	SUCCESS	2025-10-30 16:29:26.351622	\N	\N	\N
94	ORDER_CREATED	Nueva orden creada	12	Order	\N	\N	\N	SUCCESS	2025-10-30 16:47:19.495965	\N	\N	\N
95	LOGOUT	Token revocado para usuario: admin@babycash.com	31	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 17:22:52.071287	\N	\N	\N
96	LOGIN	Refresh token creado para usuario: juanitomm2408@gmail.com	32	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 18:37:28.914983	\N	\N	\N
97	LOGIN	Refresh token creado para usuario: admin@babycash.com	33	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 18:54:57.142335	\N	\N	\N
98	LOGOUT	Token revocado para usuario: admin@babycash.com	33	RefreshToken	\N	\N	\N	SUCCESS	2025-10-30 18:55:03.437078	\N	\N	\N
99	LOGIN	Refresh token creado para usuario: admin@babycash.com	34	RefreshToken	\N	\N	\N	SUCCESS	2025-10-31 15:04:42.282854	\N	\N	\N
100	LOGOUT	Token revocado para usuario: admin@babycash.com	34	RefreshToken	\N	\N	\N	SUCCESS	2025-11-04 14:33:12.675074	\N	\N	\N
101	LOGIN	Refresh token creado para usuario: admin@babycash.com	35	RefreshToken	\N	\N	\N	SUCCESS	2025-11-04 14:33:26.093303	\N	\N	\N
102	LOGOUT	Token revocado para usuario: admin@babycash.com	35	RefreshToken	\N	\N	\N	SUCCESS	2025-11-04 14:36:05.566137	\N	\N	\N
103	LOGIN	Refresh token creado para usuario: admin@babycash.com	36	RefreshToken	\N	\N	\N	SUCCESS	2025-11-04 14:36:38.5377	\N	\N	\N
104	PRODUCT_CREATED	Producto creado	73	Product	\N	\N	\N	SUCCESS	2025-11-04 14:38:24.87812	\N	\N	\N
105	ORDER_CREATED	Nueva orden creada	13	Order	\N	\N	\N	SUCCESS	2025-11-04 14:44:44.891141	\N	\N	\N
106	LOGIN	Refresh token creado para usuario: camilo@gmail.com	37	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 12:14:57.947778	\N	\N	\N
107	LOGOUT	Token revocado para usuario: camilo@gmail.com	37	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 12:15:06.455017	\N	\N	\N
108	LOGIN	Refresh token creado para usuario: camilo@gmail.com	38	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 12:15:32.668704	\N	\N	\N
109	LOGOUT	Token revocado para usuario: camilo@gmail.com	38	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 12:16:25.071998	\N	\N	\N
110	LOGIN	Refresh token creado para usuario: juanitomm2408@gmail.com	39	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 12:38:47.653034	\N	\N	\N
111	LOGIN	Refresh token creado para usuario: juanitomm2408@gmail.com	40	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 12:39:03.124558	\N	\N	\N
112	LOGIN	Refresh token creado para usuario: admin@babycash.com	41	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 12:39:57.153211	\N	\N	\N
113	LOGIN	Refresh token creado para usuario: demo@babycash.com	42	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 12:46:27.565383	\N	\N	\N
114	LOGIN	Refresh token creado para usuario: admin@babycash.com	43	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 13:39:30.591986	\N	\N	\N
115	LOGIN	Refresh token creado para usuario: juanitomm2408@gmail.com	44	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 14:26:40.570908	\N	\N	\N
116	LOGOUT	Token revocado para usuario: juanitomm2408@gmail.com	44	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 14:30:57.264532	\N	\N	\N
117	LOGIN	Refresh token creado para usuario: admin@babycash.com	45	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 14:31:09.313185	\N	\N	\N
181	PRODUCT_DELETED	Producto eliminado	43	Product	\N	\N	\N	SUCCESS	2025-11-10 12:24:12.564297	\N	\N	\N
118	LOGIN	Refresh token creado para usuario: admin@babycash.com	46	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 15:15:29.080629	\N	\N	\N
119	LOGOUT	Token revocado para usuario: admin@babycash.com	46	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 15:15:59.957189	\N	\N	\N
120	LOGIN	Refresh token creado para usuario: jmartine@arp.edo.co	47	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 15:17:40.582159	\N	\N	\N
121	LOGOUT	Token revocado para usuario: jmartine@arp.edo.co	47	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 15:18:11.864027	\N	\N	\N
122	LOGIN	Refresh token creado para usuario: jmartine@arp.edo.co	48	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 18:30:04.160989	\N	\N	\N
123	LOGOUT	Token revocado para usuario: jmartine@arp.edo.co	48	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 18:35:20.904572	\N	\N	\N
124	LOGIN	Refresh token creado para usuario: jmartine@arp.edo.co	49	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 18:36:23.119359	\N	\N	\N
125	LOGOUT	Token revocado para usuario: jmartine@arp.edo.co	49	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 18:36:58.379795	\N	\N	\N
126	LOGIN	Refresh token creado para usuario: 202215.clv@gmail.com	50	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 18:38:13.65139	\N	\N	\N
127	LOGOUT	Token revocado para usuario: 202215.clv@gmail.com	50	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 18:39:33.605265	\N	\N	\N
128	LOGOUT	Token revocado para usuario: 202215.clv@gmail.com	50	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 18:39:30.635267	\N	\N	\N
129	LOGIN	Refresh token creado para usuario: admin@babycash.com	51	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 18:59:23.530346	\N	\N	\N
130	PRODUCT_UPDATED	Producto actualizado	73	Product	\N	\N	\N	SUCCESS	2025-11-08 19:10:20.145338	\N	\N	\N
131	PRODUCT_DELETED	Producto eliminado	73	Product	\N	\N	\N	SUCCESS	2025-11-08 19:10:23.991914	\N	\N	\N
132	LOGOUT	Token revocado para usuario: admin@babycash.com	51	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 19:19:53.781778	\N	\N	\N
133	LOGIN	Refresh token creado para usuario: 202215.clv@gmail.com	52	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 19:40:20.90864	\N	\N	\N
134	ORDER_CREATED	Nueva orden creada	14	Order	\N	\N	\N	SUCCESS	2025-11-08 19:42:49.855139	\N	\N	\N
135	LOGOUT	Token revocado para usuario: 202215.clv@gmail.com	52	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 19:43:15.182433	\N	\N	\N
136	LOGIN	Refresh token creado para usuario: admin@babycash.com	53	RefreshToken	\N	\N	\N	SUCCESS	2025-11-08 19:43:25.853671	\N	\N	\N
137	PRODUCT_CREATED	Producto creado	74	Product	\N	\N	\N	SUCCESS	2025-11-08 19:47:32.019698	\N	\N	\N
138	PRODUCT_UPDATED	Producto actualizado	74	Product	\N	\N	\N	SUCCESS	2025-11-08 19:48:34.34364	\N	\N	\N
139	PRODUCT_DELETED	Producto eliminado	74	Product	\N	\N	\N	SUCCESS	2025-11-08 19:48:42.622324	\N	\N	\N
140	LOGIN	Refresh token creado para usuario: juanitomm2408@gmail.com	54	RefreshToken	\N	\N	\N	SUCCESS	2025-11-09 22:27:32.925218	\N	\N	\N
141	ORDER_CREATED	Nueva orden creada	15	Order	\N	\N	\N	SUCCESS	2025-11-09 22:29:30.803615	\N	\N	\N
142	LOGOUT	Token revocado para usuario: juanitomm2408@gmail.com	54	RefreshToken	\N	\N	\N	SUCCESS	2025-11-09 22:38:29.057567	\N	\N	\N
143	LOGIN	Refresh token creado para usuario: admin@babycash.com	55	RefreshToken	\N	\N	\N	SUCCESS	2025-11-09 22:38:38.742334	\N	\N	\N
144	LOGOUT	Token revocado para usuario: admin@babycash.com	36	RefreshToken	\N	\N	\N	SUCCESS	2025-11-09 23:16:54.843577	\N	\N	\N
145	LOGIN	Refresh token creado para usuario: admin@babycash.com	56	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 00:24:24.381533	\N	\N	\N
146	LOGOUT	Token revocado para usuario: admin@babycash.com	56	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 00:25:02.047078	\N	\N	\N
147	LOGIN	Refresh token creado para usuario: admin@babycash.com	57	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 11:56:30.741976	\N	\N	\N
148	LOGOUT	Token revocado para usuario: admin@babycash.com	57	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 11:57:29.843667	\N	\N	\N
149	LOGIN	Refresh token creado para usuario: admin@babycash.com	58	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 11:57:38.863408	\N	\N	\N
150	PRODUCT_DELETED	Producto eliminado	72	Product	\N	\N	\N	SUCCESS	2025-11-10 12:02:55.919867	\N	\N	\N
151	PRODUCT_DELETED	Producto eliminado	72	Product	\N	\N	\N	SUCCESS	2025-11-10 12:02:56.484192	\N	\N	\N
152	PRODUCT_DELETED	Producto eliminado	72	Product	\N	\N	\N	SUCCESS	2025-11-10 12:02:56.942739	\N	\N	\N
153	PRODUCT_DELETED	Producto eliminado	72	Product	\N	\N	\N	SUCCESS	2025-11-10 12:03:05.838444	\N	\N	\N
154	PRODUCT_DELETED	Producto eliminado	72	Product	\N	\N	\N	SUCCESS	2025-11-10 12:03:06.068292	\N	\N	\N
155	PRODUCT_DELETED	Producto eliminado	72	Product	\N	\N	\N	SUCCESS	2025-11-10 12:03:06.552563	\N	\N	\N
156	PRODUCT_CREATED	Producto creado	75	Product	\N	\N	\N	SUCCESS	2025-11-10 12:07:15.582472	\N	\N	\N
157	LOGOUT	Token revocado para usuario: admin@babycash.com	58	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 12:08:21.745127	\N	\N	\N
158	LOGIN	Refresh token creado para usuario: juanitomm2408@gmail.com	59	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 12:10:03.510183	\N	\N	\N
159	ORDER_CREATED	Nueva orden creada	16	Order	\N	\N	\N	SUCCESS	2025-11-10 12:13:44.86864	\N	\N	\N
160	LOGOUT	Token revocado para usuario: juanitomm2408@gmail.com	59	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 12:14:48.666512	\N	\N	\N
161	LOGIN	Refresh token creado para usuario: admin@babycash.com	60	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 12:15:03.475605	\N	\N	\N
162	PRODUCT_DELETED	Producto eliminado	75	Product	\N	\N	\N	SUCCESS	2025-11-10 12:15:19.754742	\N	\N	\N
163	PRODUCT_UPDATED	Producto actualizado	72	Product	\N	\N	\N	SUCCESS	2025-11-10 12:18:28.612393	\N	\N	\N
164	PRODUCT_UPDATED	Producto actualizado	72	Product	\N	\N	\N	SUCCESS	2025-11-10 12:19:00.650693	\N	\N	\N
165	PRODUCT_DELETED	Producto eliminado	71	Product	\N	\N	\N	SUCCESS	2025-11-10 12:19:47.489189	\N	\N	\N
166	PRODUCT_DELETED	Producto eliminado	70	Product	\N	\N	\N	SUCCESS	2025-11-10 12:19:50.998782	\N	\N	\N
167	PRODUCT_DELETED	Producto eliminado	69	Product	\N	\N	\N	SUCCESS	2025-11-10 12:19:54.614587	\N	\N	\N
168	PRODUCT_DELETED	Producto eliminado	63	Product	\N	\N	\N	SUCCESS	2025-11-10 12:19:57.453121	\N	\N	\N
169	PRODUCT_DELETED	Producto eliminado	60	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:00.116406	\N	\N	\N
170	PRODUCT_DELETED	Producto eliminado	59	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:04.410744	\N	\N	\N
171	PRODUCT_DELETED	Producto eliminado	57	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:08.155761	\N	\N	\N
172	PRODUCT_DELETED	Producto eliminado	56	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:10.227513	\N	\N	\N
173	PRODUCT_DELETED	Producto eliminado	54	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:12.475705	\N	\N	\N
174	PRODUCT_DELETED	Producto eliminado	52	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:15.182999	\N	\N	\N
175	PRODUCT_DELETED	Producto eliminado	49	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:17.913381	\N	\N	\N
176	PRODUCT_DELETED	Producto eliminado	48	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:20.181128	\N	\N	\N
177	PRODUCT_DELETED	Producto eliminado	47	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:22.429752	\N	\N	\N
178	PRODUCT_DELETED	Producto eliminado	46	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:24.727762	\N	\N	\N
179	PRODUCT_DELETED	Producto eliminado	45	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:26.491662	\N	\N	\N
180	PRODUCT_DELETED	Producto eliminado	44	Product	\N	\N	\N	SUCCESS	2025-11-10 12:20:30.557905	\N	\N	\N
182	PRODUCT_DELETED	Producto eliminado	42	Product	\N	\N	\N	SUCCESS	2025-11-10 12:24:19.749333	\N	\N	\N
184	PRODUCT_DELETED	Producto eliminado	40	Product	\N	\N	\N	SUCCESS	2025-11-10 12:24:24.645806	\N	\N	\N
183	PRODUCT_DELETED	Producto eliminado	41	Product	\N	\N	\N	SUCCESS	2025-11-10 12:24:21.970556	\N	\N	\N
185	PRODUCT_CREATED	Producto creado	76	Product	\N	\N	\N	SUCCESS	2025-11-10 12:33:03.484667	\N	\N	\N
186	PRODUCT_CREATED	Producto creado	77	Product	\N	\N	\N	SUCCESS	2025-11-10 15:42:09.065262	\N	\N	\N
187	PRODUCT_CREATED	Producto creado	78	Product	\N	\N	\N	SUCCESS	2025-11-10 15:45:22.557158	\N	\N	\N
188	PRODUCT_CREATED	Producto creado	79	Product	\N	\N	\N	SUCCESS	2025-11-10 15:47:54.489975	\N	\N	\N
189	PRODUCT_CREATED	Producto creado	80	Product	\N	\N	\N	SUCCESS	2025-11-10 15:49:56.171353	\N	\N	\N
190	LOGOUT	Token revocado para usuario: admin@babycash.com	60	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 15:50:17.98191	\N	\N	\N
191	LOGIN	Refresh token creado para usuario: mazoanas09@gmail.com	61	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 15:56:14.900791	\N	\N	\N
192	LOGOUT	Token revocado para usuario: mazoanas09@gmail.com	61	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 15:57:05.267527	\N	\N	\N
193	LOGIN	Refresh token creado para usuario: admin@babycash.com	62	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 15:58:58.882202	\N	\N	\N
194	LOGOUT	Token revocado para usuario: admin@babycash.com	62	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 16:18:04.10399	\N	\N	\N
195	LOGIN	Refresh token creado para usuario: mazoanas09@gmail.com	63	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 16:18:27.086606	\N	\N	\N
196	ORDER_CREATED	Nueva orden creada	17	Order	\N	\N	\N	SUCCESS	2025-11-10 16:19:09.970178	\N	\N	\N
197	LOGOUT	Token revocado para usuario: mazoanas09@gmail.com	63	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 16:19:16.148266	\N	\N	\N
198	LOGIN	Refresh token creado para usuario: admin@babycash.com	64	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 16:19:25.134816	\N	\N	\N
199	LOGOUT	Token revocado para usuario: admin@babycash.com	64	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 16:48:03.483815	\N	\N	\N
200	LOGIN	Refresh token creado para usuario: mazoanas09@gmail.com	65	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:10:53.082823	\N	\N	\N
201	LOGOUT	Token revocado para usuario: mazoanas09@gmail.com	65	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:11:00.583666	\N	\N	\N
202	LOGIN	Refresh token creado para usuario: admin@babycash.com	66	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:11:12.436063	\N	\N	\N
203	LOGOUT	Token revocado para usuario: admin@babycash.com	66	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:11:22.01948	\N	\N	\N
204	LOGIN	Refresh token creado para usuario: mazoanas09@gmail.com	67	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:11:30.939538	\N	\N	\N
205	LOGOUT	Token revocado para usuario: mazoanas09@gmail.com	67	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:12:48.09661	\N	\N	\N
206	LOGIN	Refresh token creado para usuario: admin@babycash.com	68	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:13:06.378685	\N	\N	\N
207	LOGOUT	Token revocado para usuario: admin@babycash.com	68	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:14:20.115098	\N	\N	\N
208	LOGIN	Refresh token creado para usuario: mazoanas09@gmail.com	69	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:15:57.551676	\N	\N	\N
209	LOGOUT	Token revocado para usuario: mazoanas09@gmail.com	69	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:17:49.350758	\N	\N	\N
210	LOGIN	Refresh token creado para usuario: admin@babycash.com	70	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:17:56.782775	\N	\N	\N
211	LOGOUT	Token revocado para usuario: admin@babycash.com	70	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:20:15.93668	\N	\N	\N
212	LOGIN	Refresh token creado para usuario: freddycardila@gmil.com	71	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:31:25.821211	\N	\N	\N
213	LOGOUT	Token revocado para usuario: freddycardila@gmil.com	71	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:31:41.346732	\N	\N	\N
214	LOGIN	Refresh token creado para usuario: freddycardila@gmail.com	72	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:33:32.712118	\N	\N	\N
215	LOGOUT	Token revocado para usuario: freddycardila@gmail.com	72	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:33:43.779684	\N	\N	\N
216	LOGIN	Refresh token creado para usuario: mazoanas09@gmail.com	73	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:36:14.38274	\N	\N	\N
217	ORDER_CREATED	Nueva orden creada	18	Order	\N	\N	\N	SUCCESS	2025-11-10 17:38:26.9094	\N	\N	\N
218	LOGOUT	Token revocado para usuario: mazoanas09@gmail.com	73	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:41:53.20155	\N	\N	\N
219	LOGIN	Refresh token creado para usuario: admin@babycash.com	74	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 17:42:04.562271	\N	\N	\N
220	PRODUCT_DELETED	Producto eliminado	80	Product	\N	\N	\N	SUCCESS	2025-11-10 17:43:34.577116	\N	\N	\N
221	PRODUCT_UPDATED	Producto actualizado	79	Product	\N	\N	\N	SUCCESS	2025-11-10 17:43:43.695278	\N	\N	\N
222	ORDER_CREATED	Nueva orden creada	19	Order	\N	\N	\N	SUCCESS	2025-11-10 20:00:21.050535	\N	\N	\N
223	LOGOUT	Token revocado para usuario: admin@babycash.com	74	RefreshToken	\N	\N	\N	SUCCESS	2025-11-10 20:01:14.986773	\N	\N	\N
\.


--
-- Data for Name: blog_comments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.blog_comments (id, approved, content, created_at, updated_at, blog_post_id, parent_comment_id, user_id) FROM stdin;
\.


--
-- Data for Name: blog_post_tags; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.blog_post_tags (blog_post_id, tag) FROM stdin;
14	ayuda
15	bebe
11	comer
\.


--
-- Data for Name: blog_posts; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.blog_posts (id, content, created_at, excerpt, featured, image_url, published, published_at, slug, title, updated_at, view_count, author_id) FROM stdin;
11	Cambia el pañal de tu bebé cada 2 o 3 horas, o inmediatamente si ha defecado, para evitar rozaduras.  Aprovecha este momento para hablarle a tu bebé, cantarle o hacerle mimos. Además de higiene, es una oportunidad ideal para fortalecer el vínculo.	2025-10-30 10:41:14.134898	El arte de cambiar un pañal	t	https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQPfnMpTgl6HgL9FdbJjvRAoEQlI7wh6SL56Q&s	t	2025-10-30 12:26:04.23607	gua-para-padres-primerizos	Guía para padres primerizos	2025-11-10 17:47:40.720408	0	10
14	aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa	2025-11-09 22:34:45.459671	aaaaaaaaaaaaaaaaaaaaaaaaa	f		f	\N	aaaaaaaaaaaaaaaaaaaaaa	aaaaaaaaaaaaaaaaaaaaaa	2025-11-09 22:34:45.459684	0	16
15	bebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebebemmmmmmmmmm	2025-11-10 12:05:27.868996	bebebebebebebebebebebebe	f	https://imgs.search.brave.com/3Fjd470Ym_0mdznYXS6nGX8L4CkCBh7tlr0DyaPo1V8/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5nZXR0eWltYWdl/cy5jb20vaWQvODMw/ODA2OTEvZnIvcGhv/dG8vamFwYW5lc2Ut/YmFieS1zbWlsaW5n/LmpwZz9zPTYxMng2/MTImdz0wJms9MjAm/Yz1kNjhJUlhHcmVE/Q0MwNEdENTBQQXNa/Y19vWWhUdnFzWEF3/ckMzWGs4R2IwPQ	t	2025-11-10 12:05:41.944335	bebes-de-5-meses	bebes de 5 meses	2025-11-10 12:05:41.945655	0	10
16	incluye exámenes básicos de salud, como pesarlo, medirlo y revisar su frecuencia cardíaca y respiración, junto con la administración de vitamina K y profilaxis ocular para prevenir infecciones e hemorragias.	2025-11-10 16:08:38.629659	Ser buena madre con el cuidado de tu bebe.	t	https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQK4sYz-ShKZjrNVV95TgyyxAyXnJeMYqIcGQ&s	t	2025-11-10 17:47:42.57648	cuidado-de-tu-bebe	Cuidado de tu bebe 	2025-11-10 19:59:12.672717	0	10
\.


--
-- Data for Name: cart_items; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.cart_items (id, added_at, quantity, cart_id, product_id) FROM stdin;
\.


--
-- Data for Name: carts; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.carts (id, created_at, updated_at, user_id) FROM stdin;
1	2025-10-30 16:13:41.456597	2025-10-30 16:13:41.456619	10
2	2025-11-08 19:40:33.397415	2025-11-08 19:40:33.397436	18
3	2025-11-09 22:29:10.503662	2025-11-09 22:29:10.503682	16
4	2025-11-10 16:18:35.866088	2025-11-10 16:18:35.866107	19
\.


--
-- Data for Name: contact_info; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.contact_info (id, address, business_hours, business_hours_details, city, company_name, country, created_at, description, email, facebook, instagram, latitude, longitude, phone, twitter, updated_at, whatsapp) FROM stdin;
1	Calle 72 #10-34	Lunes a Viernes: 8:00 AM - 6:00 PM, Sábados: 9:00 AM - 2:00 PM	\N	Bogotá D.C.	BabyCash	Colombia	2025-10-28 22:04:43.213413	Tu tienda online de confianza para productos de bebé y maternidad	202215.clv@gmail.com	https://facebook.com/babycash	https://instagram.com/babycash	\N	\N	+57 601 234 5678	https://twitter.com/babycash	2025-10-28 22:04:43.213413	+57 300 1234567
2	Calle 72 #10-34	Lunes a Viernes: 8:00 AM - 6:00 PM | Sábados: 9:00 AM - 2:00 PM | Domingos: Cerrado	\N	Bogotá D.C.	BabyCash	Colombia	2025-10-28 22:10:58.97754	Tu tienda online de confianza para productos de bebé y maternidad. Ofrecemos productos de alta calidad para el cuidado y desarrollo de tu bebé.	202215.clv@gmail.com	https://facebook.com/babycash	https://instagram.com/babycash	\N	\N	+57 601 234 5678	\N	2025-10-28 22:10:58.97754	+57 300 1234567
3	Calle 72 #10-34	Lun-Vie: 8AM-6PM, Sáb: 9AM-2PM	\N	Bogotá	BabyCash	Colombia	2025-10-28 22:13:38.907715	Tu tienda de confianza para productos de bebé y maternidad	202215.clv@gmail.com	https://facebook.com/babycash	https://instagram.com/babycash	\N	\N	+57 601 234 5678	\N	2025-10-28 22:13:38.907715	+57 300 1234567
\.


--
-- Data for Name: contact_messages; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.contact_messages (id, admin_notes, created_at, email, ip_address, message, name, phone, read_at, replied_at, status, subject, updated_at, user_agent) FROM stdin;
11	\N	2025-10-30 12:07:18.300658	admin@babycash.co	0:0:0:0:0:0:0:1	sasasasasasasasasasasasasasasasasasa	juan	5254320155	2025-10-30 12:10:23.391548	\N	READ	preocupacion	2025-10-30 12:10:23.391964	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
10	\N	2025-10-30 12:07:09.286186	demo@babycash.com	0:0:0:0:0:0:0:1	sasasasasasasasasasasasasasasa	sasasasasasasasasasasasasasasasasasasasasa	5254320155	\N	\N	ARCHIVED	preocupacion	2025-10-30 12:10:24.417213	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
13	\N	2025-11-04 14:42:06.413778	admin@babycash.com	0:0:0:0:0:0:0:1	https://imgs.search.brave.com/JoXwjrrnA4ZfcJofnMgfs8GsLjcqb72i26pF1t5jjl4/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jcnNj/aG9vbHMuYi1jZG4u/bmV0L2FwcC91cGxv/YWRzLzIwMjQvMDQv/SlNBLUJyaWNrcy1Q/UEVMLmpwZwhttps://imgs.search.brave.com/JoXwjrrnA4ZfcJofnMgfs8GsLjcqb72i26pF1t5jjl4/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jcnNj/aG9vbHMuYi1jZG4u/bmV0L2FwcC91cGxv/YWRzLzIwMjQvMDQv/SlNBLUJyaWNrcy1Q/UEVMLmpwZw	juan	5254320155	\N	\N	NEW	preocupacion	2025-11-04 14:42:06.413784	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
3	\N	2025-10-30 12:05:08.860029	demo@babycash.com	0:0:0:0:0:0:0:1	sasasasasasasasasasasasasasasa	juan	5254320155	\N	\N	NEW	preocupacion	2025-10-30 12:05:08.860034	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
4	\N	2025-10-30 12:05:38.892235	admin@babycash.com	0:0:0:0:0:0:0:1	sasasasasasasasasasasasasasasasasasasasasa	juan	5254320155	\N	\N	NEW	preocupacion	2025-10-30 12:05:38.892241	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
5	\N	2025-10-30 12:05:53.470951	admin@babycash.com	0:0:0:0:0:0:0:1	sasasasasasasasasasasasasasasa	juan	5254320155	\N	\N	NEW	preocupacion	2025-10-30 12:05:53.470958	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
6	\N	2025-10-30 12:06:16.155593	admin@babycash.com	0:0:0:0:0:0:0:1	sasasasasasasasasasasasa	juan	5254320155	\N	\N	NEW	preocupacion	2025-10-30 12:06:16.155597	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
7	\N	2025-10-30 12:06:30.864324	admin@babycash.com	0:0:0:0:0:0:0:1	sasasasasasasasasasasasa	juan	5254320155	\N	\N	NEW	preocupacion	2025-10-30 12:06:30.86433	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
8	\N	2025-10-30 12:06:43.478337	admin@babycash.com	0:0:0:0:0:0:0:1	sasasasasasasasasasasasasasasasasasa	juan	5254320155	\N	\N	NEW	preocupacion	2025-10-30 12:06:43.478341	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
9	\N	2025-10-30 12:06:56.472032	admin@babycash.com	0:0:0:0:0:0:0:1	sasasasasasasasasasasasasasasasasasa	juan	5254320155	\N	\N	NEW	preocupacion	2025-10-30 12:06:56.472036	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
14	\N	2025-11-08 19:13:33.042571	admin@babycash.co	0:0:0:0:0:0:0:1	sssssssssssasa	juan	12121212	2025-11-08 20:06:49.037517	\N	READ	preocupacion	2025-11-08 20:06:49.040242	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
12	\N	2025-10-30 12:07:43.318193	estefa@gmail.com	0:0:0:0:0:0:0:1	sasasasasasasasasasasasa	juan	5254320155	2025-10-30 12:09:11.794602	2025-10-30 12:09:11.794589	REPLIED	preocupacion	2025-10-30 12:09:11.795449	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
17	\N	2025-11-10 17:17:40.949109	mazoanas09@gmail.com	0:0:0:0:0:0:0:1	no han aceptado mi pedido 	ana 	5254320178	2025-11-10 17:18:14.096438	\N	READ	preocupacion	2025-11-10 17:18:14.097009	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
18	\N	2025-11-10 19:57:07.383613	paolitaarroyo@gmail.com	0:0:0:0:0:0:0:1	1211111111111111	PAOLA	31435465	\N	\N	NEW	pregunta	2025-11-10 19:57:07.383617	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36
\.


--
-- Data for Name: discount_usages; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.discount_usages (id, discount_id, user_id, order_id, used_at) FROM stdin;
\.


--
-- Data for Name: discounts; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.discounts (id, code, description, type, value, minimum_purchase_amount, maximum_discount_amount, usage_limit, usage_limit_per_user, applicable_to, applicable_categories, applicable_product_ids, starts_at, expires_at, active, times_used, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: loyalty_points; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.loyalty_points (id, active, amount_spent, created_at, description, expires_at, points, transaction_type, order_id, user_id) FROM stdin;
1	t	123000.00	2025-10-30 12:47:42.786459	Puntos ganados por compra #null	2026-10-30 12:47:42.783766	123	EARNED	7	10
2	t	203000.00	2025-10-30 16:48:20.742522	Puntos ganados por compra #null	2026-10-30 16:48:20.741854	203	EARNED	9	10
3	t	123000.00	2025-11-08 18:59:59.390918	Puntos ganados por compra #null	2026-11-08 18:59:59.379529	123	EARNED	10	10
4	t	169000.00	2025-11-08 19:52:21.197888	Puntos ganados por compra #null	2026-11-08 19:52:21.175348	169	EARNED	14	18
5	t	70000.00	2025-11-10 20:00:50.505652	Puntos ganados por compra #null	2026-11-10 20:00:50.502253	70	EARNED	19	10
\.


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.notifications (id, user_id, title, message, type, metadata, reference_type, reference_id, read, read_at, created_at) FROM stdin;
\.


--
-- Data for Name: order_items; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.order_items (id, quantity, subtotal, unit_price, order_id, product_id) FROM stdin;
7	1	5.00	5.00	6	69
8	1	2.00	2.00	6	70
9	1	123000.00	123000.00	7	71
10	1	35000.00	35000.00	8	56
11	1	50000.00	50000.00	8	72
12	1	155000.00	155000.00	9	57
13	1	48000.00	48000.00	9	59
14	1	123000.00	123000.00	10	71
15	1	35000.00	35000.00	11	56
16	1	50000.00	50000.00	11	72
17	1	123000.00	123000.00	11	71
18	1	62000.00	62000.00	11	60
19	1	123000.00	123000.00	12	71
20	3	144000.00	48000.00	13	59
21	2	124000.00	62000.00	14	60
22	1	45000.00	45000.00	14	63
23	1	155000.00	155000.00	15	57
24	1	45000.00	45000.00	16	63
25	1	20000.00	20000.00	17	78
26	1	40000.00	40000.00	17	76
27	1	50000.00	50000.00	17	72
28	1	20000.00	20000.00	18	78
29	1	40000.00	40000.00	18	76
30	1	20000.00	20000.00	19	78
31	1	50000.00	50000.00	19	72
\.


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.orders (id, created_at, notes, order_number, shipping_address, status, total_amount, updated_at, user_id) FROM stdin;
6	2025-10-30 12:38:05.76105	2121212	\N	juan martinez | Tel: 5254320155 | caller 32323, bogota, cundinamarca, CP: 12122	PENDING	7.00	2025-10-30 12:38:05.815719	10
7	2025-10-30 12:47:04.743299	1222	\N	juan martinez | Tel: 5254320155 | caller 32323, bogota, cundinamarca, CP: 12122	DELIVERED	123000.00	2025-10-30 12:47:42.793506	10
8	2025-10-30 12:55:45.412067	121212	\N	juan martinez | Tel: 5254320155 | caller 32323, bogota, cundinamarca, CP: 12122	PENDING	85000.00	2025-10-30 12:55:45.432465	10
18	2025-11-10 17:38:26.884004	sdfghjkertyuighjk	\N	valentina | Tel: 5254320178 | caller 32323, bogota, cundinamarca, CP: 12122	SHIPPED	60000.00	2025-11-10 17:45:57.070526	19
11	2025-10-30 16:29:26.33355	2122222	\N	juan martinez | Tel: 12121212 | caller 32323, bogota, cundinamarca, CP: 12122	PENDING	270000.00	2025-10-30 16:29:26.360336	10
12	2025-10-30 16:47:19.439977	1212	\N	juan martinez | Tel: 5254320155 | caller 32323, bogota, cundinamarca, CP: 12122	PENDING	123000.00	2025-10-30 16:47:19.531549	10
9	2025-10-30 13:15:57.694115	222	\N	juan martinez | Tel: 5254320155 | caller 32323, bogota, cundinamarca, CP: 12122	DELIVERED	203000.00	2025-10-30 16:48:20.749806	10
10	2025-10-30 16:14:01.601176	12121212	\N	juan martinez | Tel: 5254320155 | caller 32323, bogota, cundinamarca, CP: 12122	DELIVERED	123000.00	2025-11-08 18:59:59.514942	10
19	2025-11-10 20:00:21.041321	jhjhjhjh	\N	juan martinez | Tel: 12121212 | caller 32323, bogota, cundinamarca, CP: 12122	DELIVERED	70000.00	2025-11-10 20:00:50.510475	10
13	2025-11-04 14:44:44.8817	qwqwqw	\N	juan martinez | Tel: 5254320155 | caller 32323, bogota, cundinamarca, CP: 12122	CANCELLED	144000.00	2025-11-08 19:03:05.984711	10
14	2025-11-08 19:42:49.834044	5 piso	\N	valentina | Tel: 5254320155 | caller 32323, bogota, cundinamarca, CP: 12122	DELIVERED	169000.00	2025-11-08 19:52:21.204491	18
15	2025-11-09 22:29:30.522819	Método de pago: cash_on_delivery	\N	juan martinez | Tel: 5254320155 | caller 32323, bogota, cundinamarca, CP: 12122	PROCESSING	155000.00	2025-11-10 11:59:56.773648	16
16	2025-11-10 12:13:44.848902	qwertyuiop[]asdfghjkl;'\\	\N	juan martinez | Tel: 5254320155 | caller 32323, bogota, cundinamarca, CP: 12122	PENDING	45000.00	2025-11-10 12:13:44.867251	16
17	2025-11-10 16:19:09.956942	jaAJAJAJAJJAJAJAJAJAJJAJAJAJAJAJAJAJAJAJAJAJJAA	\N	juan martinez | Tel: 12121212 | caller 32323, bogota, cundinamarca, CP: 12122	SHIPPED	110000.00	2025-11-10 16:19:39.372066	19
\.


--
-- Data for Name: payments; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.payments (id, amount, created_at, metadata, method, status, transaction_id, updated_at, order_id) FROM stdin;
\.


--
-- Data for Name: product_variants; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.product_variants (id, product_id, name, sku, size, color, material, price_adjustment, stock, active, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.products (id, category, created_at, description, discount_price, enabled, featured, image_url, name, price, rating, review_count, stock, updated_at) FROM stdin;
71	OTHER	2025-10-30 12:21:48.328097	ssssssssssss	\N	f	f	https://imgs.search.brave.com/rYALYjFY1J09rWFwUoqsTPAUENW9SaD41dMiWa4fop8/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvMTA5/MzA4OTk1NC9lcy9m/b3RvL2hvbWJyZS10/aWVuZS1sYS1wYXRh/LWRlbC1wZXJyby1j/b24tYW1vci1jb3Jh/em9uZXMtcm9qb3Mt/c29icmUtZm9uZG8t/YmxhbmNvLmpwZz9z/PTYxMng2MTImdz0w/Jms9MjAmYz1QYTkt/OXJuTGdEWVMwa054/bjhUZGVhblVNOUhw/bnktTlQxYUpmbXFB/d0k4PQ	assasw	123000.00	0.00	0	0	2025-11-10 12:19:47.486516
78	FOOD	2025-11-10 15:45:22.551512	Tarro de leche Nestogeno 800g 	20000.00	t	f	https://d2d21jw8en5l3a.cloudfront.net/vendty2_db_23656_bebi2020/imagenes_productos/5097_leche_nestogeno_1_x800g_imagen.jpeg	Leche Nestogeno	40000.00	0.00	0	12	2025-11-10 20:00:21.048105
75	ACCESSORIES	2025-11-10 12:07:15.577099	nneennneennneennneen	10000.00	f	f	https://imgs.search.brave.com/3Fjd470Ym_0mdznYXS6nGX8L4CkCBh7tlr0DyaPo1V8/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5nZXR0eWltYWdl/cy5jb20vaWQvODMw/ODA2OTEvZnIvcGhv/dG8vamFwYW5lc2Ut/YmFieS1zbWlsaW5n/LmpwZz9zPTYxMng2/MTImdz0wJms9MjAm/Yz1kNjhJUlhHcmVE/Q0MwNEdENTBQQXNa/Y19vWWhUdnFzWEF3/ckMzWGs4R2IwPQ	bebe	12000.00	0.00	0	100	2025-11-10 12:15:19.750681
72	HEALTHCARE	2025-10-30 12:23:01.877974	Panales etapa 2 	50000.00	t	f	https://imgs.search.brave.com/kQgHJsYg19Qb8nRK2SOgS8iUGYa16L2vnbM88rHpllA/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9pbWFn/ZW5lcy5lbHBhaXMu/Y29tL3Jlc2l6ZXIv/djIvWUcyTzJERVda/UkFPUENEVFhPSkE1/NE1TREkucG5nP2F1/dGg9M2YwOWJiMmFk/ZTNjMmJiMWVmZjZh/MDJjYTRiM2UwODJl/NGU0OTQ4ZjE1YWNi/YzY4YTMzZDg2OTZk/ZjU3ZjRjNiZ3aWR0/aD00MTQ	panal	85000.00	0.00	0	49	2025-11-10 20:00:21.054481
44	TOYS	2025-10-29 14:11:49.60972	Sonajero de madera natural con cascabeles. Estimula el desarrollo sensorial y motor. Diseño ergonómico fácil de agarrar. Certificado libre de tóxicos.	\N	f	f	https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=500	Sonajero Musical Montessori	28000.00	0.00	0	45	2025-11-10 12:20:30.557379
43	CLOTHING	2025-10-29 14:11:49.607404	Pack de 6 pares de calcetines con suela antideslizante. Elástico suave que no aprieta. Diseños divertidos y coloridos. Tallas 0-24 meses.	\N	f	f	https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=500	Calcetines Antideslizantes Pack x6	32000.00	0.00	0	60	2025-11-10 12:24:12.562239
49	FOOD	2025-10-29 14:11:49.621527	Cereal de arroz fortificado con hierro y vitaminas. Fácil digestión. Sin gluten. Ideal como primer alimento sólido. Caja de 400g.	\N	f	f	https://images.unsplash.com/photo-1505576399279-565b52d4ac71?w=500	Cereal Infantil Fortificado	24000.00	0.00	0	40	2025-11-10 12:20:17.912824
48	FOOD	2025-10-29 14:11:49.619418	Pack de 6 frascos de papilla 100% orgánica. Sin azúcares añadidos, sin conservantes. Para bebés de 6+ meses. Frutas cultivadas localmente.	\N	f	f	https://images.unsplash.com/photo-1502301103665-0b95cc738daf?w=500	Papilla Orgánica Manzana y Pera	38000.00	0.00	0	55	2025-11-10 12:20:20.180322
47	TOYS	2025-10-29 14:11:49.616816	Osito de peluche hipoalergénico con proyector de estrellas y 10 melodías relajantes. Temporizador automático. Ayuda al bebé a dormir tranquilo.	78000.00	f	f	https://images.unsplash.com/photo-1530325553241-4f6e7690cf36?w=500	Peluche Musical Luz Nocturna	95000.00	0.00	0	20	2025-11-10 12:20:22.42878
46	TOYS	2025-10-29 14:11:49.614322	Set de 6 cubos blandos de diferentes tamaños y colores. Incluyen números, letras y texturas. Seguros y lavables. Ideales para bebés de 6 meses+.	\N	f	f	https://images.unsplash.com/photo-1596461404969-9ae70f2830c1?w=500	Cubos Apilables de Tela	42000.00	0.00	0	30	2025-11-10 12:20:24.727233
45	TOYS	2025-10-29 14:11:49.612139	Gimnasio con arco ajustable, espejo seguro, 5 juguetes colgantes y música. Alfombra acolchada lavable. Estimula el desarrollo motor y visual del bebé.	155000.00	f	f	https://images.unsplash.com/photo-1567359781514-3b964e2b04d6?w=500	Gimnasio de Actividades Musical	185000.00	0.00	0	15	2025-11-10 12:20:26.49112
42	CLOTHING	2025-10-29 14:11:49.605254	Set incluye: gorro, body y pantalón. Material 100% algodón hipoalergénico. Perfecto para regalo de baby shower. Disponible en varios colores.	65000.00	f	f	https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=500	Conjunto 3 Piezas Recién Nacido	78000.00	0.00	0	25	2025-11-10 12:24:19.748685
41	CLOTHING	2025-10-29 14:11:49.602794	Pijama enteriza en algodón suave con pies antideslizantes. Cierre frontal con broches para facilitar el cambio de pañal. Tallas 0-12 meses.	\N	f	f	https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=500	Pijama Bebé con Piecitos	52000.00	0.00	0	35	2025-11-10 12:24:21.969637
40	CLOTHING	2025-10-29 14:11:49.598333	Set de 3 bodies de algodón 100% orgánico, suaves y cómodos para la piel del bebé. Disponibles en colores pastel: blanco, rosa y celeste.	38000.00	f	f	https://images.unsplash.com/photo-1519689373023-dd07c7988603?w=500	Body de Algodón Manga Corta	45000.00	0.00	0	50	2025-11-10 12:24:24.642118
76	ACCESSORIES	2025-11-10 12:33:03.480173	Baberos de tela	\N	t	f	https://imgs.search.brave.com/Chr1mN6yqMcQzp6rGKAYt48ecgSFcjuB3bLukPytyDQ/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/aWtlYS5jb20vZXMv/ZXMvaW1hZ2VzL3By/b2R1Y3RzL2d1bGR2/YXZhcmUtYmFiZXJv/LXZlcmRlLWJsYW5j/b19fMTMzNDUxMV9w/ZTk0Njc1NF9zNS5q/cGc_Zj14eHM	Baberos	40000.00	0.00	0	28	2025-11-10 19:58:16.179913
79	FOOD	2025-11-10 15:47:54.483274	Tetero para nino	\N	t	t	https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRuYbTrhQmajGCNYOjyPT81U_3OmTzn9jqXFg&s	Teteros 123	15000.00	0.00	0	80	2025-11-10 19:58:29.712399
59	HEALTHCARE	2025-10-29 14:11:49.645271	Set completo: cortaúñas, lima, cepillo, peine, tijeras punta roma, aspirador nasal, cepillo dental, masajeador encías. Estuche organizador incluido.	\N	f	f	https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=500	Kit de Aseo Bebé 10 Piezas	48000.00	0.00	0	31	2025-11-10 12:20:04.40928
57	ACCESSORIES	2025-10-29 14:11:49.640724	Portabebé ajustable con soporte lumbar. 4 posiciones de carga. Tela transpirable. Distribuye el peso uniformemente. Soporta hasta 20kg. Varios colores.	155000.00	f	f	https://images.unsplash.com/photo-1519689373023-dd07c7988603?w=500	Portabebé Ergonómico Premium	185000.00	0.00	0	16	2025-11-10 12:20:08.152967
56	ACCESSORIES	2025-10-29 14:11:49.638001	Set de 5 baberos con bolsillo recolector. Material impermeable fácil de limpiar. Cierre ajustable. Diseños coloridos y divertidos. Libre de BPA.	\N	f	f	https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=500	Baberos Impermeables Pack x5	35000.00	0.00	0	63	2025-11-10 12:20:10.227086
54	ACCESSORIES	2025-10-29 14:11:49.633012	Pack de 2 chupetes de silicona médica. Diseño ortodóntico que respeta el desarrollo bucal. Incluye caja esterilizadora. Sin BPA. 0-6 meses.	\N	f	f	https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=500	Chupetes Ortodónticos Pack x2	22000.00	0.00	0	80	2025-11-10 12:20:12.474303
52	FURNITURE	2025-10-29 14:11:49.62786	Mueble cambiador con 3 gavetas espaciosas. Superficie acolchada impermeable. Barandas de seguridad. Altura ergonómica. Madera resistente al agua.	\N	f	f	https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=500	Cambiador de Pañales con Gavetas	420000.00	0.00	0	12	2025-11-10 12:20:15.182276
80	HEALTHCARE	2025-11-10 15:49:56.156	Panales winny etapa 5	\N	f	f	https://ecommerce.surtifamiliar.com/backend/admin/backend/web/archivosDelCliente/items/images/20241118100139-Higiene-Desechable-Panales-Ninos-Panal-Winny-x30und-Etapa-5-15315202411181001396032.webp	Panal	50000.00	0.00	0	70	2025-11-10 17:43:34.576615
70	OTHER	2025-10-29 20:59:06.182519	gaugaugaugau	2.00	f	f	https://imgs.search.brave.com/0RXQXlXKdw1Qu7lWVmsdPPVHQoOsa8EQTNcdRwnWaOg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9pbWcu/ZnJlZXBpay5jb20v/Zm90by1ncmF0aXMv/cGVycm8tcGVxdWVu/by1zaWVuZG8tYWRv/cmFibGUtZXN0dWRp/b18yMy0yMTQ5MDE2/ODg1LmpwZz9zZW10/PWFpc19oeWJyaWQm/dz03NDAmcT04MA	juan 	1234242.00	0.00	0	1	2025-11-10 12:19:50.997916
69	BOOKS	2025-10-29 20:58:37.645024	oooooooooooooooooooooo	5.00	f	f	https://imgs.search.brave.com/0RXQXlXKdw1Qu7lWVmsdPPVHQoOsa8EQTNcdRwnWaOg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9pbWcu/ZnJlZXBpay5jb20v/Zm90by1ncmF0aXMv/cGVycm8tcGVxdWVu/by1zaWVuZG8tYWRv/cmFibGUtZXN0dWRp/b18yMy0yMTQ5MDE2/ODg1LmpwZz9zZW10/PWFpc19oeWJyaWQm/dz03NDAmcT04MA	estefa	123000.00	0.00	0	1	2025-11-10 12:19:54.611366
63	BOOKS	2025-10-29 14:11:49.65291	Libro de cartón grueso con 5 cuentos clásicos adaptados. Ilustraciones grandes y coloridas. Esquinas redondeadas. Perfecto para lectura antes de dormir.	\N	f	f	https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=500	Cuentos Clásicos para Bebés de dos meces	45000.00	0.00	0	33	2025-11-10 12:19:57.450527
77	FOOD	2025-11-10 15:42:09.046787	2 teteros para nina	\N	t	f	https://imgs.search.brave.com/_1fF78UqQgdzHafbFZIdgnc0Cn_rcuUWeQmNYFtwEp0/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9leGl0/b2NvbC52dGV4aW1n/LmNvbS5ici9hcnF1/aXZvcy9pZHMvMTk2/MjI3NDgva2l0LWRl/LXRldGVyb3MtcGFy/YS1iZWJlLW5pYS1i/ZWItMTAyODI0NDU0/My5qcGc_dj02Mzgy/OTYxOTkxNjY0MDAw/MDA	Teteros	30000.00	0.00	0	10	2025-11-10 19:58:15.066655
60	HEALTHCARE	2025-10-29 14:11:49.64703	Aspirador nasal suave y efectivo. 3 niveles de succión. Silencioso. Boquillas de silicona lavables. Funciona con baterías. Fácil de limpiar.	\N	f	f	https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=500	Aspirador Nasal Eléctrico	62000.00	0.00	0	22	2025-11-10 12:20:00.11459
\.


--
-- Data for Name: refresh_tokens; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.refresh_tokens (id, created_at, expiry_date, ip_address, revoked, revoked_at, token, user_agent, user_id) FROM stdin;
24	2025-10-29 20:03:56.534669	2025-11-05 20:03:56.53426	0:0:0:0:0:0:0:1	t	2025-10-29 20:04:58.630679	6ad6cab3-2033-4325-a442-4ae86c776407	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	11
25	2025-10-29 20:05:06.049816	2025-11-05 20:05:06.049329	0:0:0:0:0:0:0:1	t	2025-10-29 20:26:36.874429	38dc3c07-0aea-4975-9337-142937404f54	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
10	2025-10-29 16:03:02.88067	2025-11-05 16:03:02.880259	0:0:0:0:0:0:0:1	f	\N	aa081c59-ad3a-4254-ac5e-2509af9698b8	curl/8.16.0	11
11	2025-10-29 16:04:35.217818	2025-11-05 16:04:35.217381	0:0:0:0:0:0:0:1	f	\N	4cba8ebc-9d4f-47a5-a36d-5572557ae2e2	curl/8.16.0	13
12	2025-10-29 16:04:45.337022	2025-11-05 16:04:45.336505	0:0:0:0:0:0:0:1	f	\N	a0f46696-9667-4375-b80e-31c0a44ae3c8	curl/8.16.0	13
13	2025-10-29 16:05:05.480053	2025-11-05 16:05:05.479507	0:0:0:0:0:0:0:1	t	2025-10-29 16:11:45.081454	464660ff-06c5-4bc5-a260-17af4516081b	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	11
15	2025-10-29 19:15:25.785742	2025-11-05 19:15:25.774798	0:0:0:0:0:0:0:1	t	2025-10-29 19:15:50.909722	f108e3dd-d0ab-4cb7-a004-ead63cd1341f	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	11
16	2025-10-29 19:16:00.937917	2025-11-05 19:16:00.937438	0:0:0:0:0:0:0:1	t	2025-10-29 19:16:22.439558	35ed2697-d0be-4728-a50f-7e4991d9a6fd	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	11
8	2025-10-29 15:42:43.009269	2025-11-05 15:42:42.990544	0:0:0:0:0:0:0:1	t	2025-10-29 19:44:20.055024	8e8c1a80-bca1-4e03-94e7-c59c1468e7c0	curl/8.16.0	10
20	2025-10-29 19:44:32.542412	2025-11-05 19:44:32.541224	0:0:0:0:0:0:0:1	f	\N	f134a9ec-f3b1-419b-affa-2b0f4f02e8c0	curl/8.16.0	10
9	2025-10-29 15:56:34.676582	2025-11-05 15:56:34.676154	0:0:0:0:0:0:0:1	t	2025-10-29 19:44:32.539916	1e72493e-b45b-4298-b62d-5a2e6b107e76	curl/8.16.0	10
21	2025-10-29 19:47:37.516103	2025-11-05 19:47:37.515345	0:0:0:0:0:0:0:1	f	\N	a1bf263e-d55c-4779-9335-4b78b9ceff45	curl/8.16.0	10
14	2025-10-29 16:12:02.736654	2025-11-05 16:12:02.736235	0:0:0:0:0:0:0:1	t	2025-10-29 19:47:37.514374	e2468817-2df6-4a68-9459-21adffc8fb9e	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
17	2025-10-29 19:16:35.05336	2025-11-05 19:16:35.052876	0:0:0:0:0:0:0:1	t	2025-10-29 19:53:33.91649	9caff819-ed58-4d8f-9da8-479772fa403e	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
22	2025-10-29 19:55:28.522273	2025-11-05 19:55:28.521891	0:0:0:0:0:0:0:1	t	2025-10-29 19:56:05.520895	27f297b8-c8ed-42e4-84e2-80650140dd4a	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	11
23	2025-10-29 19:56:18.41485	2025-11-05 19:56:18.414456	0:0:0:0:0:0:0:1	t	2025-10-29 19:57:46.951279	58e70b1e-bc60-4e53-b141-1fac5a6b0718	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
26	2025-10-29 20:27:02.669456	2025-11-05 20:27:02.668752	0:0:0:0:0:0:0:1	t	2025-10-30 06:55:32.260135	3da89a13-5a44-4ef0-ac6e-99b06ab56d49	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
27	2025-10-30 06:55:42.427619	2025-11-06 06:55:42.427298	0:0:0:0:0:0:0:1	t	2025-10-30 06:58:43.539	c20e8c4a-272e-4372-9407-a991b691b845	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	11
28	2025-10-30 06:58:59.268187	2025-11-06 06:58:59.267858	0:0:0:0:0:0:0:1	f	\N	2fe1cd60-7dc3-4a2d-af17-ccb01d59c8e3	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
29	2025-10-30 16:07:56.239211	2025-11-06 16:07:56.167559	0:0:0:0:0:0:0:1	t	2025-10-30 16:08:38.046849	22fb2728-8b10-411c-9fb4-f348e3d8ff57	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	11
30	2025-10-30 16:08:48.626283	2025-11-06 16:08:48.625315	0:0:0:0:0:0:0:1	f	\N	705df70a-a940-4062-a442-3354838f5342	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
18	2025-10-29 19:23:14.816627	2025-11-05 19:23:14.804959	0:0:0:0:0:0:0:1	t	2025-10-30 16:08:48.624594	ed1e8f3c-ecee-4fa8-9d14-6a1222f79a32	curl/8.16.0	10
19	2025-10-29 19:44:20.060911	2025-11-05 19:44:20.055944	0:0:0:0:0:0:0:1	t	2025-10-30 16:28:09.744631	e7f5e15a-6f3e-4d05-bb8b-fce72a26d1a9	curl/8.16.0	10
31	2025-10-30 16:28:09.797992	2025-11-06 16:28:09.763465	0:0:0:0:0:0:0:1	t	2025-10-30 17:22:51.977138	4f1fd0ed-00c3-4632-ba49-2b564f740110	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
33	2025-10-30 18:54:57.101641	2025-11-06 18:54:57.091844	0:0:0:0:0:0:0:1	t	2025-10-30 18:55:03.411495	878d1f26-9f90-4119-befb-d521b3a95200	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
34	2025-10-31 15:04:41.964807	2025-11-07 15:04:41.851382	0:0:0:0:0:0:0:1	t	2025-11-04 14:33:12.562912	5dffb191-1b3a-44c9-aa8e-cde9db8c3042	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
35	2025-11-04 14:33:26.080136	2025-11-11 14:33:26.077767	0:0:0:0:0:0:0:1	t	2025-11-04 14:36:05.542218	76dd7ab7-ee4c-4f2d-a14e-4d2fda80c6b8	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
37	2025-11-08 12:14:57.911715	2025-11-15 12:14:57.910042	0:0:0:0:0:0:0:1	t	2025-11-08 12:15:06.341771	ad6e2c1b-2886-42f3-8e86-fc8ff6e2de70	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	15
38	2025-11-08 12:15:32.66265	2025-11-15 12:15:32.661658	0:0:0:0:0:0:0:1	t	2025-11-08 12:16:25.059728	1b832d31-350a-409c-af60-a2b40025e5c8	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	15
39	2025-11-08 12:38:47.641911	2025-11-15 12:38:47.640458	0:0:0:0:0:0:0:1	f	\N	8bc9f68b-758a-4ab9-939b-fc9376e158fe	curl/8.16.0	16
40	2025-11-08 12:39:03.118001	2025-11-15 12:39:03.117416	0:0:0:0:0:0:0:1	f	\N	15302392-146b-4dbf-8a4f-5066e0d8a2d8	curl/8.16.0	16
42	2025-11-08 12:46:27.541428	2025-11-15 12:46:27.535509	0:0:0:0:0:0:0:1	f	\N	4bf0d5b0-c9f8-4640-8af7-392a03b0ebf1	curl/8.16.0	11
43	2025-11-08 13:39:30.300257	2025-11-15 13:39:30.293709	0:0:0:0:0:0:0:1	f	\N	d663a295-f3dd-4008-a6a5-ad2cac70e769	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
44	2025-11-08 14:26:40.527695	2025-11-15 14:26:40.514763	0:0:0:0:0:0:0:1	t	2025-11-08 14:30:57.060011	a4fdb73e-4a1a-44ba-b599-fe385f020e71	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	16
45	2025-11-08 14:31:09.306524	2025-11-15 14:31:09.305335	0:0:0:0:0:0:0:1	f	\N	1e5c12b0-7f64-404e-a1d0-45d55ba90e9a	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
46	2025-11-08 15:15:29.044321	2025-11-15 15:15:29.031125	0:0:0:0:0:0:0:1	t	2025-11-08 15:15:59.943456	56b8b75d-11fa-4817-98f2-b19c5722df91	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
41	2025-11-08 12:39:57.149316	2025-11-15 12:39:57.148749	0:0:0:0:0:0:0:1	t	2025-11-10 00:24:24.342611	d2c9e9c8-3d23-4b52-9c6a-e77fd9fd9e82	curl/8.16.0	10
36	2025-11-04 14:36:38.5337	2025-11-11 14:36:38.533054	0:0:0:0:0:0:0:1	t	2025-11-09 23:16:54.814026	a61c03d8-736e-412c-a3af-d1bd09762e2a	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
47	2025-11-08 15:17:40.567365	2025-11-15 15:17:40.564263	0:0:0:0:0:0:0:1	t	2025-11-08 15:18:11.834205	1e47911e-8fee-4301-9896-a242800072c9	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	17
48	2025-11-08 18:30:04.116041	2025-11-15 18:30:04.084539	0:0:0:0:0:0:0:1	t	2025-11-08 18:35:20.880904	53c6fa6a-69a0-4711-bed9-bb9159747038	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	17
49	2025-11-08 18:36:23.112276	2025-11-15 18:36:23.111897	0:0:0:0:0:0:0:1	t	2025-11-08 18:36:58.355038	d188de30-92ef-4e73-b976-23bf5220e619	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	17
50	2025-11-08 18:38:13.648501	2025-11-15 18:38:13.64798	0:0:0:0:0:0:0:1	t	2025-11-08 18:39:33.501951	6e1ea29e-a656-4445-b236-f78aeee450b6	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	18
51	2025-11-08 18:59:23.506497	2025-11-15 18:59:23.49754	0:0:0:0:0:0:0:1	t	2025-11-08 19:19:53.765138	17bc6a08-1272-4f9c-b25c-a6b4981f1c29	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
52	2025-11-08 19:40:20.88359	2025-11-15 19:40:20.881079	0:0:0:0:0:0:0:1	t	2025-11-08 19:43:15.136975	2d826276-3dce-4926-bc2e-7d04b102ff67	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	18
53	2025-11-08 19:43:25.843509	2025-11-15 19:43:25.842979	0:0:0:0:0:0:0:1	f	\N	e8d9d7d1-417c-48e2-8fd3-0b14209b7ff6	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
54	2025-11-09 22:27:32.896293	2025-11-16 22:27:32.889131	0:0:0:0:0:0:0:1	t	2025-11-09 22:38:29.051249	2152201e-6423-488d-a176-159e165ea450	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	16
55	2025-11-09 22:38:38.739613	2025-11-16 22:38:38.739294	0:0:0:0:0:0:0:1	f	\N	a175e145-eeee-4073-834c-db4d42f1da76	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
56	2025-11-10 00:24:24.351812	2025-11-17 00:24:24.344778	0:0:0:0:0:0:0:1	t	2025-11-10 00:25:02.038434	b33857cb-24f5-4a07-ad88-dff09505e66e	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
57	2025-11-10 11:56:30.658299	2025-11-17 11:56:30.639973	0:0:0:0:0:0:0:1	t	2025-11-10 11:57:29.814862	ca73d33e-c3d3-4587-aa3b-d1367aba2501	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
58	2025-11-10 11:57:38.859308	2025-11-17 11:57:38.858709	0:0:0:0:0:0:0:1	t	2025-11-10 12:08:21.726854	4f656aec-b095-45e5-a1be-ce8ff4780aab	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
59	2025-11-10 12:10:03.50695	2025-11-17 12:10:03.506397	0:0:0:0:0:0:0:1	t	2025-11-10 12:14:48.645562	d31ceb52-e4a8-48ce-a934-4f7c5b42ef28	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	16
60	2025-11-10 12:15:03.468679	2025-11-17 12:15:03.467934	0:0:0:0:0:0:0:1	t	2025-11-10 15:50:17.970582	64c23bfa-91f2-4b78-a4c4-8da9c6bc8e0a	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
61	2025-11-10 15:56:14.889812	2025-11-17 15:56:14.889104	0:0:0:0:0:0:0:1	t	2025-11-10 15:57:05.250807	6fdaa704-4643-4d07-a1b1-82752f70bdb0	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	19
62	2025-11-10 15:58:58.878362	2025-11-17 15:58:58.877929	0:0:0:0:0:0:0:1	t	2025-11-10 16:18:04.066032	53e869c6-7921-41b1-af88-b633c46b8d85	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
63	2025-11-10 16:18:27.075511	2025-11-17 16:18:27.074863	0:0:0:0:0:0:0:1	t	2025-11-10 16:19:16.130454	4767a87e-b3be-4f61-9a9e-895ff96a63ac	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	19
64	2025-11-10 16:19:25.125986	2025-11-17 16:19:25.125566	0:0:0:0:0:0:0:1	t	2025-11-10 16:48:03.4337	3ce867e8-885d-4b60-b137-77cbfb05940f	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
65	2025-11-10 17:10:53.07762	2025-11-17 17:10:53.07686	0:0:0:0:0:0:0:1	t	2025-11-10 17:11:00.561283	2960886c-5333-4431-a952-f3041c74ec9f	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	19
66	2025-11-10 17:11:12.431962	2025-11-17 17:11:12.431586	0:0:0:0:0:0:0:1	t	2025-11-10 17:11:22.005681	4d26b0fe-3e12-49e5-850b-3ef1e9847fe2	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
67	2025-11-10 17:11:30.934505	2025-11-17 17:11:30.933865	0:0:0:0:0:0:0:1	t	2025-11-10 17:12:48.075627	c5f94a50-bca1-423f-a41e-8eb64222b404	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	19
68	2025-11-10 17:13:06.375445	2025-11-17 17:13:06.375066	0:0:0:0:0:0:0:1	t	2025-11-10 17:14:20.106463	2d1be404-f361-478d-abc7-b095fe2ebcbd	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
69	2025-11-10 17:15:57.548493	2025-11-17 17:15:57.548122	0:0:0:0:0:0:0:1	t	2025-11-10 17:17:49.345956	c9594457-3f70-4f86-9dd9-8e187c8e3a4b	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	19
70	2025-11-10 17:17:56.778515	2025-11-17 17:17:56.778111	0:0:0:0:0:0:0:1	t	2025-11-10 17:20:15.926957	b81ef8f8-7502-490d-add1-ac7cc014b08a	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
71	2025-11-10 17:31:25.817088	2025-11-17 17:31:25.816686	0:0:0:0:0:0:0:1	t	2025-11-10 17:31:41.338787	79e20480-74bf-4c6d-a546-f65ef374dd16	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	20
72	2025-11-10 17:33:32.708377	2025-11-17 17:33:32.707944	0:0:0:0:0:0:0:1	t	2025-11-10 17:33:43.759321	4c4fdbea-6654-45d3-b226-1fed7a7d5419	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	21
73	2025-11-10 17:36:14.377778	2025-11-17 17:36:14.377309	0:0:0:0:0:0:0:1	t	2025-11-10 17:41:53.191161	45ebf28d-38ab-40be-a254-4ff0e7d7398e	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	19
74	2025-11-10 17:42:04.556886	2025-11-17 17:42:04.556587	0:0:0:0:0:0:0:1	t	2025-11-10 20:01:14.97392	6fb1dfb0-86fe-403c-9f9a-30e2d182b67e	Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36	10
\.


--
-- Data for Name: review_votes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.review_votes (id, review_id, user_id, vote_type, created_at) FROM stdin;
\.


--
-- Data for Name: reviews; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.reviews (id, product_id, user_id, order_id, rating, title, comment, image_urls, verified_purchase, upvotes, downvotes, approved, flagged, admin_response, admin_response_at, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: testimonials; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.testimonials (id, approved, avatar, created_at, featured, location, message, name, rating, updated_at) FROM stdin;
14	t	https://i.pravatar.cc/150?img=14	2025-10-29 14:11:49.678916	f	Barranquilla, Colombia	El monitor de bebé con cámara HD superó mis expectativas. La visión nocturna es excelente y la batería dura muchísimo. Muy satisfecho con la compra.	Jorge Andrés Silva	5	2025-10-30 11:10:38.13387
15	t	https://i.pravatar.cc/150?img=9	2025-10-29 14:11:49.680951	f	Cartagena, Colombia	Compré la cuna colecho y es perfecta. Fácil de instalar, muy segura y el diseño es hermoso. Mi bebé duerme muy tranquilo.	Laura Martínez	5	2025-10-30 11:10:39.848825
18	t		2025-10-30 11:09:31.357108	t	bogota	muy mala no me gusta nadanadanadanadanadanadanada	juan	1	2025-10-30 11:10:56.875855
19	t		2025-10-30 11:10:01.793971	t	nadanadanadanadanadanada	nadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanadanada	nadanadanadanada	1	2025-10-30 11:10:58.075441
22	t		2025-10-30 12:04:23.135166	f	ddddddddddddddddddddddd	dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd	ssssssssssssssssssssssssss	3	2025-10-30 12:26:54.679252
21	t		2025-10-30 12:04:10.86112	f	asasasa	asaaaaaassssssssssssssssssssssss	asasasa	3	2025-10-30 12:26:55.255631
17	t	https://i.pravatar.cc/150?img=10	2025-10-29 14:11:49.685596	f	Santa Marta, Colombia	El coche travel system 3 en 1 es una maravilla. Muy práctico, fácil de usar y de excelente calidad. Vale cada peso.	Isabella García	5	2025-10-30 12:26:56.374564
16	f	https://i.pravatar.cc/150?img=13	2025-10-29 14:11:49.682992	f	Bucaramanga, Colombia	Excelente atención al cliente. Tuve una consulta sobre tallas y me respondieron súper rápido. Los productos son de muy buena calidad.	Pedro Sánchez	4	2025-10-29 20:40:23.250505
23	f	https://imgs.search.brave.com/xcVdqPaeaH3-oW9val1pKD4K7cdHr59b0tbfY7HuGDs/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9lbGNv/bWVyY2lvLnBlL3Jl/c2l6ZXIvdjIvRVpY/MzdYWE5JSkFaVkJH/STRHSTVIS1FYS1ku/anBnP2F1dGg9MDQw/MWU4ZmI2OGM2NTky/ZDllMzE3MTlkM2Uy/OWI1NmJjNjk0ZjY5/NDE2NmI3ZGU4ZjIy/YzRkYTQ4YmEwNjRm/NiZ3aWR0aD02ODAm/aGVpZ2h0PTY4MCZx/dWFsaXR5PTc1JnNt/YXJ0PXRydWU	2025-11-08 18:32:51.069495	f	bogota	bienbienbienbienbienbien	paola macias	5	2025-11-10 12:05:46.714445
11	f	https://i.pravatar.cc/150?img=1	2025-10-29 14:11:49.671777	f	Bogotá, Colombia	Excelente servicio, los productos llegaron en perfecto estado y muy rápido. La calidad de la ropa es excepcional, mi bebé se ve hermoso y cómodo.	María Rodríguez	5	2025-10-29 20:40:24.529058
25	t		2025-11-10 17:12:38.392863	t	bogota	exelente, la ame wow \nme encanta \nla mejor pagina jejejejejjeje	ana sofia 	5	2025-11-10 17:14:00.809968
26	t		2025-11-10 17:40:59.693313	f	bogota	mal servicio no contestan se pierden las cosas 	crolina	2	2025-11-10 19:59:29.795523
13	t	https://i.pravatar.cc/150?img=5	2025-10-29 14:11:49.676804	f	Cali, Colombia	Me encantó el conjunto de recién nacido que compré para mi baby shower. La calidad es premium y el precio muy justo. Recomendado 100%.	Ana Patricia López	5	2025-10-30 11:10:37.218662
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (id, created_at, email, email_verified, enabled, first_name, last_name, password, phone, refresh_token, reset_password_token, role, updated_at, verification_token, address, reset_password_expiry) FROM stdin;
16	2025-11-08 12:38:47.452697	juanitomm2408@gmail.com	f	t	Juan	Martinez	$2a$12$egeZv1ZRjxVqtaWe4hyRneE4m8jdFvMPzCSA37akg4GuKHuskZO.q	\N	\N	\N	USER	2025-11-10 12:09:43.414538	\N	\N	\N
10	2025-10-29 14:11:49.545651	admin@babycash.com	f	t	Administrador	Sistema	$2a$12$EbjLXrCIg.RTjRn8PkagYe5QGHVZqk.VwPnaL2xJhghGYu1Jn41Tu	6458978529	\N	a2188e93-38b9-4347-ad4a-8402132af2c2	ADMIN	2025-11-10 12:12:48.164342	\N	87878	2025-10-30 19:21:45.377514
19	2025-11-10 15:56:14.872484	mazoanas09@gmail.com	f	t	ana 	mazo	$2a$12$d3pqtzkkSqkMYi/3h6F38OFgcKPZCk3VqBv1uodXiSnfg3DAC5XyO	3219297605	\N	\N	USER	2025-11-10 15:56:14.872499	\N	\N	\N
11	2025-10-29 14:11:49.575491	demo@babycash.com	f	t	Usuario	Demo	$2a$12$BYSBEyoKZSQETuxPY6gybuK48AfK2.NQd.LLtYwyqSmFRi6KLCccm	\N	\N	\N	USER	2025-10-29 14:11:49.575602	\N	\N	\N
12	2025-10-29 14:11:49.577778	maria.garcia@example.com	f	t	María	García	$2a$12$BYSBEyoKZSQETuxPY6gybuK48AfK2.NQd.LLtYwyqSmFRi6KLCccm	\N	\N	\N	USER	2025-10-29 14:11:49.577804	\N	\N	\N
13	2025-10-29 16:04:35.209323	estefa@gmail.com	f	t	Estefanía	Prueba	$2a$12$BYSBEyoKZSQETuxPY6gybuK48AfK2.NQd.LLtYwyqSmFRi6KLCccm	\N	\N	\N	USER	2025-10-29 16:04:35.209393	\N	\N	\N
15	2025-11-08 12:14:57.548988	camilo@gmail.com	f	t	camilo	fernandez de vanegas	$2a$12$BYSBEyoKZSQETuxPY6gybuK48AfK2.NQd.LLtYwyqSmFRi6KLCccm	5254320143	\N	\N	USER	2025-11-08 12:14:57.549037	\N	\N	\N
17	2025-11-08 15:17:40.527975	jmartine@arp.edo.co	f	t	juan	ppppp	$2a$12$PZZK6VEknuD9VmdzmJ0pV.plmm0KcukjRkmsY9ks.PRRvUrnuu0Wa	5254320178	\N	84bace3d-baca-41fd-bcc0-38275d904fda	USER	2025-11-08 15:18:37.345701	\N	\N	2025-11-08 16:18:37.343712
18	2025-11-08 18:38:13.64134	202215.clv@gmail.com	f	t	valentina	riuz	$2a$12$LZMPya3Nl5B/Hoo8BSTDuugVCBLUFRj8r05Yc5WbDHGZNrlALlX4e	3204482812	\N	ee5f582b-5710-433c-9827-031eda3192ac	USER	2025-11-08 18:39:46.513963	\N	\N	2025-11-08 19:39:46.509233
20	2025-11-10 17:31:25.81095	freddycardila@gmil.com	f	t	freddy	ardila	$2a$12$67tO1fAXQ5IHboTcVtQqn.ehCIO5sv6P.9zKUbl7jlVnb8hoaXSlK	3204567892	\N	120484	USER	2025-11-10 17:32:11.305098	\N	\N	2025-11-10 17:47:11.301505
21	2025-11-10 17:33:32.69986	freddycardila@gmail.com	f	t	freddy	ardila	$2a$12$ngjwjLrwQpmCR93lvXkLeerfSd7g8R.rXEn8F.gm70.TNx0xEEpta	3209876543	\N	\N	USER	2025-11-10 17:35:41.552189	\N	\N	\N
\.


--
-- Data for Name: wishlist_items; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.wishlist_items (id, wishlist_id, product_id, created_at) FROM stdin;
\.


--
-- Data for Name: wishlists; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.wishlists (id, user_id, name, description, is_public, created_at, updated_at) FROM stdin;
\.


--
-- Name: addresses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.addresses_id_seq', 1, false);


--
-- Name: audit_logs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.audit_logs_id_seq', 223, true);


--
-- Name: blog_comments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.blog_comments_id_seq', 3, true);


--
-- Name: blog_posts_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.blog_posts_id_seq', 16, true);


--
-- Name: cart_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.cart_items_id_seq', 20, true);


--
-- Name: carts_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.carts_id_seq', 4, true);


--
-- Name: contact_info_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.contact_info_id_seq', 3, true);


--
-- Name: contact_messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.contact_messages_id_seq', 18, true);


--
-- Name: discount_usages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.discount_usages_id_seq', 1, false);


--
-- Name: discounts_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.discounts_id_seq', 1, false);


--
-- Name: loyalty_points_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.loyalty_points_id_seq', 5, true);


--
-- Name: notifications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.notifications_id_seq', 1, false);


--
-- Name: order_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.order_items_id_seq', 31, true);


--
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.orders_id_seq', 19, true);


--
-- Name: payments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.payments_id_seq', 1, false);


--
-- Name: product_variants_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.product_variants_id_seq', 1, false);


--
-- Name: products_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.products_id_seq', 80, true);


--
-- Name: refresh_tokens_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.refresh_tokens_id_seq', 74, true);


--
-- Name: review_votes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.review_votes_id_seq', 1, false);


--
-- Name: reviews_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.reviews_id_seq', 1, false);


--
-- Name: testimonials_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.testimonials_id_seq', 26, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_id_seq', 21, true);


--
-- Name: wishlist_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.wishlist_items_id_seq', 1, false);


--
-- Name: wishlists_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.wishlists_id_seq', 1, false);


--
-- Name: addresses addresses_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT addresses_pkey PRIMARY KEY (id);


--
-- Name: audit_logs audit_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT audit_logs_pkey PRIMARY KEY (id);


--
-- Name: blog_comments blog_comments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.blog_comments
    ADD CONSTRAINT blog_comments_pkey PRIMARY KEY (id);


--
-- Name: blog_posts blog_posts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.blog_posts
    ADD CONSTRAINT blog_posts_pkey PRIMARY KEY (id);


--
-- Name: cart_items cart_items_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT cart_items_pkey PRIMARY KEY (id);


--
-- Name: carts carts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.carts
    ADD CONSTRAINT carts_pkey PRIMARY KEY (id);


--
-- Name: contact_info contact_info_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contact_info
    ADD CONSTRAINT contact_info_pkey PRIMARY KEY (id);


--
-- Name: contact_messages contact_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.contact_messages
    ADD CONSTRAINT contact_messages_pkey PRIMARY KEY (id);


--
-- Name: discount_usages discount_usages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.discount_usages
    ADD CONSTRAINT discount_usages_pkey PRIMARY KEY (id);


--
-- Name: discounts discounts_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.discounts
    ADD CONSTRAINT discounts_code_key UNIQUE (code);


--
-- Name: discounts discounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.discounts
    ADD CONSTRAINT discounts_pkey PRIMARY KEY (id);


--
-- Name: loyalty_points loyalty_points_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loyalty_points
    ADD CONSTRAINT loyalty_points_pkey PRIMARY KEY (id);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- Name: order_items order_items_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.order_items
    ADD CONSTRAINT order_items_pkey PRIMARY KEY (id);


--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- Name: payments payments_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT payments_pkey PRIMARY KEY (id);


--
-- Name: product_variants product_variants_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_variants
    ADD CONSTRAINT product_variants_pkey PRIMARY KEY (id);


--
-- Name: product_variants product_variants_sku_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.product_variants
    ADD CONSTRAINT product_variants_sku_key UNIQUE (sku);


--
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- Name: refresh_tokens refresh_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);


--
-- Name: review_votes review_votes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.review_votes
    ADD CONSTRAINT review_votes_pkey PRIMARY KEY (id);


--
-- Name: reviews reviews_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reviews
    ADD CONSTRAINT reviews_pkey PRIMARY KEY (id);


--
-- Name: testimonials testimonials_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.testimonials
    ADD CONSTRAINT testimonials_pkey PRIMARY KEY (id);


--
-- Name: carts uk64t7ox312pqal3p7fg9o503c2; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.carts
    ADD CONSTRAINT uk64t7ox312pqal3p7fg9o503c2 UNIQUE (user_id);


--
-- Name: users uk6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: payments uk8vo36cen604as7etdfwmyjsxt; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT uk8vo36cen604as7etdfwmyjsxt UNIQUE (order_id);


--
-- Name: blog_posts ukfmrqlsu8hgt4xyp3ewt66h287; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.blog_posts
    ADD CONSTRAINT ukfmrqlsu8hgt4xyp3ewt66h287 UNIQUE (slug);


--
-- Name: refresh_tokens ukghpmfn23vmxfu3spu3lfg4r2d; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT ukghpmfn23vmxfu3spu3lfg4r2d UNIQUE (token);


--
-- Name: payments uklryndveuwa4k5qthti0pkmtlx; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT uklryndveuwa4k5qthti0pkmtlx UNIQUE (transaction_id);


--
-- Name: orders uknthkiu7pgmnqnu86i2jyoe2v7; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT uknthkiu7pgmnqnu86i2jyoe2v7 UNIQUE (order_number);


--
-- Name: review_votes uq_review_votes_user_review; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.review_votes
    ADD CONSTRAINT uq_review_votes_user_review UNIQUE (user_id, review_id);


--
-- Name: reviews uq_reviews_user_product; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reviews
    ADD CONSTRAINT uq_reviews_user_product UNIQUE (user_id, product_id);


--
-- Name: wishlist_items uq_wishlist_items_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.wishlist_items
    ADD CONSTRAINT uq_wishlist_items_unique UNIQUE (wishlist_id, product_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: wishlist_items wishlist_items_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.wishlist_items
    ADD CONSTRAINT wishlist_items_pkey PRIMARY KEY (id);


--
-- Name: wishlists wishlists_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.wishlists
    ADD CONSTRAINT wishlists_pkey PRIMARY KEY (id);


--
-- Name: idx_addresses_is_default; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_addresses_is_default ON public.addresses USING btree (user_id, is_default) WHERE (is_default = true);


--
-- Name: idx_addresses_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_addresses_user_id ON public.addresses USING btree (user_id);


--
-- Name: idx_audit_action; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_audit_action ON public.audit_logs USING btree (action_type);


--
-- Name: idx_audit_entity; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_audit_entity ON public.audit_logs USING btree (entity_type, entity_id);


--
-- Name: idx_audit_timestamp; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_audit_timestamp ON public.audit_logs USING btree ("timestamp");


--
-- Name: idx_audit_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_audit_user ON public.audit_logs USING btree (user_id);


--
-- Name: idx_discount_usages_discount_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_discount_usages_discount_id ON public.discount_usages USING btree (discount_id);


--
-- Name: idx_discount_usages_order_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_discount_usages_order_id ON public.discount_usages USING btree (order_id);


--
-- Name: idx_discount_usages_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_discount_usages_user_id ON public.discount_usages USING btree (user_id);


--
-- Name: idx_discounts_active; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_discounts_active ON public.discounts USING btree (active) WHERE (active = true);


--
-- Name: idx_discounts_code; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_discounts_code ON public.discounts USING btree (code);


--
-- Name: idx_discounts_dates; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_discounts_dates ON public.discounts USING btree (starts_at, expires_at);


--
-- Name: idx_notifications_created_at; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_notifications_created_at ON public.notifications USING btree (created_at DESC);


--
-- Name: idx_notifications_read; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_notifications_read ON public.notifications USING btree (user_id, read) WHERE (read = false);


--
-- Name: idx_notifications_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_notifications_type ON public.notifications USING btree (type);


--
-- Name: idx_notifications_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_notifications_user_id ON public.notifications USING btree (user_id);


--
-- Name: idx_refresh_expiry; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_refresh_expiry ON public.refresh_tokens USING btree (expiry_date);


--
-- Name: idx_refresh_token; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_refresh_token ON public.refresh_tokens USING btree (token);


--
-- Name: idx_refresh_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_refresh_user ON public.refresh_tokens USING btree (user_id);


--
-- Name: idx_review_votes_review_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_review_votes_review_id ON public.review_votes USING btree (review_id);


--
-- Name: idx_review_votes_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_review_votes_user_id ON public.review_votes USING btree (user_id);


--
-- Name: idx_reviews_approved; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_reviews_approved ON public.reviews USING btree (approved) WHERE (approved = true);


--
-- Name: idx_reviews_created_at; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_reviews_created_at ON public.reviews USING btree (created_at DESC);


--
-- Name: idx_reviews_helpfulness; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_reviews_helpfulness ON public.reviews USING btree (((upvotes - downvotes)) DESC);


--
-- Name: idx_reviews_product_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_reviews_product_id ON public.reviews USING btree (product_id);


--
-- Name: idx_reviews_rating; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_reviews_rating ON public.reviews USING btree (rating);


--
-- Name: idx_reviews_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_reviews_user_id ON public.reviews USING btree (user_id);


--
-- Name: idx_reviews_verified_purchase; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_reviews_verified_purchase ON public.reviews USING btree (verified_purchase) WHERE (verified_purchase = true);


--
-- Name: idx_variants_active; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_variants_active ON public.product_variants USING btree (active);


--
-- Name: idx_variants_product_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_variants_product_id ON public.product_variants USING btree (product_id);


--
-- Name: idx_wishlist_items_product_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_wishlist_items_product_id ON public.wishlist_items USING btree (product_id);


--
-- Name: idx_wishlist_items_wishlist_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_wishlist_items_wishlist_id ON public.wishlist_items USING btree (wishlist_id);


--
-- Name: idx_wishlists_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_wishlists_user_id ON public.wishlists USING btree (user_id);


--
-- Name: addresses trg_addresses_update_timestamp; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_addresses_update_timestamp BEFORE UPDATE ON public.addresses FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- Name: discounts trg_discounts_update_timestamp; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_discounts_update_timestamp BEFORE UPDATE ON public.discounts FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- Name: product_variants trg_product_variants_update_timestamp; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_product_variants_update_timestamp BEFORE UPDATE ON public.product_variants FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- Name: review_votes trg_review_votes_update_counts; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_review_votes_update_counts AFTER INSERT OR DELETE OR UPDATE ON public.review_votes FOR EACH ROW EXECUTE FUNCTION public.update_review_votes();


--
-- Name: reviews trg_reviews_update_timestamp; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_reviews_update_timestamp BEFORE UPDATE ON public.reviews FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- Name: wishlists trg_wishlists_update_timestamp; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_wishlists_update_timestamp BEFORE UPDATE ON public.wishlists FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- Name: cart_items fk1re40cjegsfvw58xrkdp6bac6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT fk1re40cjegsfvw58xrkdp6bac6 FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- Name: orders fk32ql8ubntj5uh44ph9659tiih; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT fk32ql8ubntj5uh44ph9659tiih FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: payments fk81gagumt0r8y3rmudcgpbk42l; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT fk81gagumt0r8y3rmudcgpbk42l FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- Name: blog_post_tags fk9lwi4pg2kl7ce7pa3r3yotb9w; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.blog_post_tags
    ADD CONSTRAINT fk9lwi4pg2kl7ce7pa3r3yotb9w FOREIGN KEY (blog_post_id) REFERENCES public.blog_posts(id);


--
-- Name: discount_usages fk_discount_usages_discount; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.discount_usages
    ADD CONSTRAINT fk_discount_usages_discount FOREIGN KEY (discount_id) REFERENCES public.discounts(id) ON DELETE CASCADE;


--
-- Name: refresh_tokens fk_refresh_token_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: review_votes fk_review_votes_review; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.review_votes
    ADD CONSTRAINT fk_review_votes_review FOREIGN KEY (review_id) REFERENCES public.reviews(id) ON DELETE CASCADE;


--
-- Name: wishlist_items fk_wishlist_items_wishlist; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.wishlist_items
    ADD CONSTRAINT fk_wishlist_items_wishlist FOREIGN KEY (wishlist_id) REFERENCES public.wishlists(id) ON DELETE CASCADE;


--
-- Name: carts fkb5o626f86h46m4s7ms6ginnop; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.carts
    ADD CONSTRAINT fkb5o626f86h46m4s7ms6ginnop FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: order_items fkbioxgbv59vetrxe0ejfubep1w; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.order_items
    ADD CONSTRAINT fkbioxgbv59vetrxe0ejfubep1w FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- Name: blog_comments fkbovv268mg0vg57pkp0nb1bkq4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.blog_comments
    ADD CONSTRAINT fkbovv268mg0vg57pkp0nb1bkq4 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: blog_comments fkhfcfe7kposiby2u55l8d39fqr; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.blog_comments
    ADD CONSTRAINT fkhfcfe7kposiby2u55l8d39fqr FOREIGN KEY (blog_post_id) REFERENCES public.blog_posts(id);


--
-- Name: loyalty_points fkjx52acv9c5myhf3y1vmrufayb; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loyalty_points
    ADD CONSTRAINT fkjx52acv9c5myhf3y1vmrufayb FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: blog_posts fklog64k5g2l1679hjl2wuyyk5n; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.blog_posts
    ADD CONSTRAINT fklog64k5g2l1679hjl2wuyyk5n FOREIGN KEY (author_id) REFERENCES public.users(id);


--
-- Name: loyalty_points fklsm6njoij1rr7o56mwgncqe5d; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loyalty_points
    ADD CONSTRAINT fklsm6njoij1rr7o56mwgncqe5d FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- Name: order_items fkocimc7dtr037rh4ls4l95nlfi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.order_items
    ADD CONSTRAINT fkocimc7dtr037rh4ls4l95nlfi FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- Name: cart_items fkpcttvuq4mxppo8sxggjtn5i2c; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT fkpcttvuq4mxppo8sxggjtn5i2c FOREIGN KEY (cart_id) REFERENCES public.carts(id);


--
-- Name: blog_comments fkrt9ek0aspkel4ef2fiv7x6w79; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.blog_comments
    ADD CONSTRAINT fkrt9ek0aspkel4ef2fiv7x6w79 FOREIGN KEY (parent_comment_id) REFERENCES public.blog_comments(id);


--
-- PostgreSQL database dump complete
--

\unrestrict M4M8qhYGhTaMmhDvVCSZsLzaL6BKZQ1BvMifObARZGw9Lk7qxRFe0jg3zlJopCq

