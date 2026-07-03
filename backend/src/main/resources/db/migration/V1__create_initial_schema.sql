CREATE TYPE "transaction_type" AS ENUM (
  'PIX',
  'TRANSFER',
  'DEPOSIT',
  'WITHDRAWAL'
);

CREATE TYPE "transaction_status" AS ENUM (
  'COMPLETED',
  'FAILED',
  'REVERSED'
);

CREATE TYPE "scheduled_payment_recurrence" AS ENUM (
  'NONE',
  'WEEKLY',
  'MONTHLY'
);

CREATE TYPE "scheduled_payment_status" AS ENUM (
  'PENDING',
  'PROCESSED',
  'FAILED',
  'CANCELLED'
);

CREATE TYPE "pix_key_type" AS ENUM (
  'CPF',
  'EMAIL',
  'PHONE',
  'RANDOM'
);

CREATE TYPE "notification_type" AS ENUM (
  'TRANSFER_RECEIVED',
  'PAYMENT_PROCESSED',
  'PAYMENT_FAILED',
  'LOGIN_ALERT'
);

CREATE TABLE "users" (
                         "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
                         "name" varchar(255) NOT NULL,
                         "cpf" varchar(11) UNIQUE NOT NULL,
                         "email" varchar(255) UNIQUE NOT NULL,
                         "phone" varchar(20) UNIQUE,
                         "password" varchar(255) NOT NULL,
                         "is_active" boolean NOT NULL DEFAULT true,
                         "created_at" timestamp NOT NULL DEFAULT (now()),
                         "updated_at" timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE "accounts" (
                            "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
                            "user_id" uuid NOT NULL,
                            "account_number" varchar(10) UNIQUE NOT NULL,
                            "branch" varchar(4) NOT NULL DEFAULT '0001',
                            "balance" numeric(16,2) NOT NULL DEFAULT 0,
                            "is_active" boolean NOT NULL DEFAULT true,
                            "created_at" timestamp NOT NULL DEFAULT (now()),
                            "updated_at" timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE "pix_keys" (
                            "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
                            "account_id" uuid NOT NULL,
                            "key_type" pix_key_type NOT NULL,
                            "key_value" varchar(255) UNIQUE NOT NULL,
                            "is_active" boolean NOT NULL DEFAULT true,
                            "created_at" timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE "transactions" (
                                "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
                                "account_origin_id" uuid,
                                "account_destination_id" uuid,
                                "amount" numeric(16,2) NOT NULL,
                                "type" transaction_type NOT NULL,
                                "status" transaction_status NOT NULL DEFAULT 'COMPLETED',
                                "description" varchar(255),
                                "created_at" timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE "scheduled_payments" (
                                      "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
                                      "account_origin_id" uuid NOT NULL,
                                      "account_destination_id" uuid NOT NULL,
                                      "amount" numeric(16,2) NOT NULL,
                                      "description" varchar(255),
                                      "scheduled_date" date NOT NULL,
                                      "recurrence" scheduled_payment_recurrence NOT NULL DEFAULT 'NONE',
                                      "status" scheduled_payment_status NOT NULL DEFAULT 'PENDING',
                                      "last_processed_at" timestamp,
                                      "created_at" timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE "notifications" (
                                 "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
                                 "user_id" uuid NOT NULL,
                                 "title" varchar(255) NOT NULL,
                                 "message" text NOT NULL,
                                 "type" notification_type NOT NULL,
                                 "is_read" boolean NOT NULL DEFAULT false,
                                 "created_at" timestamp NOT NULL DEFAULT (now())
);

CREATE TABLE "login_attempts" (
                                  "id" uuid PRIMARY KEY DEFAULT (gen_random_uuid()),
                                  "email" varchar(255) NOT NULL,
                                  "ip_address" varchar(45),
                                  "success" boolean NOT NULL,
                                  "attempted_at" timestamp NOT NULL DEFAULT (now())
);

CREATE INDEX "idx_transactions_account_origin" ON "transactions" ("account_origin_id");

CREATE INDEX "idx_transactions_account_destination" ON "transactions" ("account_destination_id");

CREATE INDEX "idx_transactions_created_at" ON "transactions" ("created_at");

CREATE INDEX "idx_scheduled_payments_status" ON "scheduled_payments" ("status");

CREATE INDEX "idx_scheduled_payments_scheduled_date" ON "scheduled_payments" ("scheduled_date");

CREATE INDEX "idx_notifications_user_id" ON "notifications" ("user_id");

CREATE INDEX "idx_notifications_is_read" ON "notifications" ("is_read");

ALTER TABLE "accounts" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "pix_keys" ADD FOREIGN KEY ("account_id") REFERENCES "accounts" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "transactions" ADD FOREIGN KEY ("account_origin_id") REFERENCES "accounts" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "transactions" ADD FOREIGN KEY ("account_destination_id") REFERENCES "accounts" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "scheduled_payments" ADD FOREIGN KEY ("account_origin_id") REFERENCES "accounts" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "scheduled_payments" ADD FOREIGN KEY ("account_destination_id") REFERENCES "accounts" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "notifications" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") DEFERRABLE INITIALLY IMMEDIATE;
