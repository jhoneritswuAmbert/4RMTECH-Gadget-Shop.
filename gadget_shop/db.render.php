<?php

try {
    $databaseUrl = trim(getenv('DATABASE_URL') ?: '');
    $parts = parse_url($databaseUrl);

    if (
        !$parts ||
        !in_array($parts['scheme'] ?? '', ['postgres', 'postgresql'], true) ||
        !isset($parts['host'], $parts['user'], $parts['pass'], $parts['path'])
    ) {
        throw new RuntimeException('Invalid database configuration.');
    }

    $host = $parts['host'];
    $port = $parts['port'] ?? 5432;
    $database = rawurldecode(ltrim($parts['path'], '/'));
    $username = rawurldecode($parts['user']);
    $password = rawurldecode($parts['pass']);

    if (
        !preg_match('/^[A-Za-z0-9.-]+$/D', $host) ||
        !preg_match('/^[A-Za-z0-9_.-]+$/D', $database)
    ) {
        throw new RuntimeException('Invalid database configuration.');
    }

    parse_str($parts['query'] ?? '', $query);
    if (($query['channel_binding'] ?? '') === 'require') {
        putenv('PGCHANNELBINDING=require');
    }

    $dsn = "pgsql:host={$host};port={$port};dbname={$database};sslmode=require";

    $conn = new PDO($dsn, $username, $password, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
    ]);

    $conn->exec("SET TIME ZONE 'Asia/Manila'");
} catch (Throwable $e) {
    error_log('Cloud database connection failed. Code: ' . $e->getCode());
    http_response_code(503);
    header('Content-Type: application/json');
    echo json_encode([
        'success' => false,
        'message' => 'Database connection is unavailable.'
    ]);
    exit;
}
