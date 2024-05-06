package com.example.tadm.api.base

import com.example.tadm.api.Config

class Urls
{
    companion object
    {
        // const val URL = "https://project.maxoduke.dev/api"
        private const val BASE_URL = Config.BASE_URL


        const val GETPERSON_DETAILS = "$BASE_URL/fillDeatils"
    }
}