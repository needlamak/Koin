package com.koin.data.coin

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.koin.domain.coin.Coin

// DTO to Domain
fun CoinDetailDto.toDomain(): Coin {
    return Coin(
        id = id,
        name = name,
        symbol = symbol,
        imageUrl = image,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        priceChange24h = priceChange24h,
        priceChangePercentage24h = priceChangePercentage24h,
        priceChangePercentage1h = priceChangePercentage1h,
        priceChangePercentage7d = priceChangePercentage7d,
        priceChangePercentage30d = priceChangePercentage30d,
        sparklineData = sparklineIn7d?.price,
        high24h = high24h,
        low24h = low24h,
        totalVolume = totalVolume,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        athDate = athDate,
        atl = atl,
        atlDate = atlDate
    )
}

// Domain to Entity
fun Coin.toEntity(): CoinEntity {
    return CoinEntity(
        id = id,
        name = name,
        symbol = symbol,
        imageUrl = imageUrl,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        priceChange24h = priceChange24h,
        priceChangePercentage24h = priceChangePercentage24h,
        priceChangePercentage1h = priceChangePercentage1h,
        priceChangePercentage7d = priceChangePercentage7d,
        priceChangePercentage30d = priceChangePercentage30d,
        sparklineData = sparklineData?.let { Gson().toJson(it) },
        high24h = high24h,
        low24h = low24h,
        totalVolume = totalVolume,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        athDate = athDate,
        atl = atl,
        atlDate = atlDate
    )
}

// Entity to Domain
fun CoinEntity.toDomain(): Coin {
    return Coin(
        id = id,
        name = name,
        symbol = symbol,
        imageUrl = imageUrl,
        currentPrice = currentPrice,
        marketCap = marketCap,
        marketCapRank = marketCapRank,
        priceChange24h = priceChange24h,
        priceChangePercentage24h = priceChangePercentage24h,
        priceChangePercentage1h = priceChangePercentage1h,
        priceChangePercentage7d = priceChangePercentage7d,
        priceChangePercentage30d = priceChangePercentage30d,
        sparklineData = sparklineData?.let {
            Gson().fromJson(it, object : TypeToken<List<Double>>() {}.type)
        },
        high24h = high24h,
        low24h = low24h,
        totalVolume = totalVolume,
        circulatingSupply = circulatingSupply,
        totalSupply = totalSupply,
        maxSupply = maxSupply,
        ath = ath,
        athDate = athDate,
        atl = atl,
        atlDate = atlDate
    )
} 