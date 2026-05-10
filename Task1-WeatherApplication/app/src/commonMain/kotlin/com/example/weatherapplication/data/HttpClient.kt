package com.example.weatherapplication.data

import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient
