package com.yourname.yournameeats;

import com.yourname.yournameeats.util.PasswordUtil;

/**
 * One-time throwaway script to generate real bcrypt hashes for seed.sql.
 * Run this once (right-click Run in your IDE), copy the output into seed.sql,
 * then you can delete this file — it is NOT part of the actual application.
 */
public class HashGenerator {
    public static void main(String[] args) {
        System.out.println("admin123 -> " + PasswordUtil.hash("admin123"));
        System.out.println("owner123 -> " + PasswordUtil.hash("owner123"));
        System.out.println("customer123 -> " + PasswordUtil.hash("customer123"));
    }
}
