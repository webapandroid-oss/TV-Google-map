package com.example.model

enum class MapLayerType(val label: String, val description: String) {
    STANDARD("Standard", "Clear vector roads and city boundaries"),
    SATELLITE("Satellite", "High-resolution orbital & aerial imagery"),
    TERRAIN("Terrain", "Topographic elevation contours & mountain peaks"),
    NIGHT("Night Cyber", "High contrast dark mode for cinematic TV viewing")
}

enum class MapOverlay(val label: String) {
    TRAFFIC("Live Traffic"),
    TRANSIT("Transit Lines"),
    LANDMARKS("Landmark Pins"),
    GRID("Coordinates Grid")
}

enum class TvInteractionMode(val title: String, val badge: String) {
    MAP_PAN("Map Pan Mode", "PANNING MAP (D-Pad glides map)"),
    MENU_FOCUS("Menu Navigation", "MENU FOCUS (D-Pad selects buttons)")
}

enum class LocationCategory(val label: String) {
    ALL("All Categories"),
    RESTAURANTS("Restaurants"),
    COFFEE("Coffee & Cafes"),
    SHOPS("Shops & Boutiques"),
    SUPERMARKETS("Supermarkets"),
    SHOWROOMS("Showrooms & Auto"),
    WONDERS("World Wonders"),
    CITIES("Megacities"),
    NATURE("Nature & Peaks"),
    SERVICES("Gas & Services")
}

data class LocationPoint(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val defaultZoom: Float,
    val category: LocationCategory,
    val description: String,
    val trivia: String,
    val elevationMeters: Int,
    val imageUrl: String,
    val plusCode: String = "",
    val rating: Double = 4.8
)

data class NavigationRoute(
    val title: String,
    val origin: LocationPoint,
    val destination: LocationPoint,
    val distanceKm: Double,
    val flightHours: Double,
    val waypointsCount: Int
)

object MapDataRepository {

    // Current TV Device Location (Requested Plus Code & City)
    val myDeviceLocation = LocationPoint(
        id = "my_current_location",
        name = "My TV Device Location",
        city = "Dhaka",
        country = "Bangladesh",
        latitude = 23.8103,
        longitude = 90.4125,
        defaultZoom = 15f,
        category = LocationCategory.CITIES,
        description = "Current connected Android TV device location.",
        trivia = "Your current device location: PBW+QJJG, Dhaka 1318, Bangladesh.",
        elevationMeters = 12,
        imageUrl = "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=600&auto=format&fit=crop&q=80",
        plusCode = "PBW+QJJG, Dhaka 1318, Bangladesh",
        rating = 5.0
    )

    val curatedLocations = listOf(
        myDeviceLocation,

        // Quick Category: Restaurants
        LocationPoint(
            id = "restaurant_kasturi",
            name = "Kasturi Heritage Restaurant",
            city = "Dhaka",
            country = "Bangladesh",
            latitude = 23.7371,
            longitude = 90.4172,
            defaultZoom = 15f,
            category = LocationCategory.RESTAURANTS,
            description = "Celebrated Bengali culinary landmark known for traditional fish curries, bhortas, and aromatic rice dishes.",
            trivia = "A favorite gastronomic destination in Purana Paltan serving authentic heritage recipes since the 1970s.",
            elevationMeters = 14,
            imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600&auto=format&fit=crop&q=80",
            plusCode = "PRG8+RF Dhaka",
            rating = 4.7
        ),
        LocationPoint(
            id = "restaurant_french_laundry",
            name = "The French Laundry",
            city = "California",
            country = "United States",
            latitude = 38.4045,
            longitude = -122.3647,
            defaultZoom = 15f,
            category = LocationCategory.RESTAURANTS,
            description = "Three-Michelin-starred French restaurant by Chef Thomas Keller in the heart of Napa Valley.",
            trivia = "No single ingredient is ever repeated throughout its legendary 12-course tasting menu.",
            elevationMeters = 85,
            imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600&auto=format&fit=crop&q=80",
            plusCode = "CJM8+44 Yountville, CA",
            rating = 4.9
        ),
        LocationPoint(
            id = "restaurant_jules_verne",
            name = "Le Jules Verne (Eiffel Tower)",
            city = "Paris",
            country = "France",
            latitude = 48.8584,
            longitude = 2.2945,
            defaultZoom = 15f,
            category = LocationCategory.RESTAURANTS,
            description = "Michelin-starred gourmet dining suspended 125 metres high on the second floor of the Eiffel Tower.",
            trivia = "Features private elevators directly ascending from the South pillar to the restaurant floor.",
            elevationMeters = 125,
            imageUrl = "https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?w=600&auto=format&fit=crop&q=80",
            plusCode = "V75W+9R Paris",
            rating = 4.8
        ),

        // Quick Category: Coffee & Cafes
        LocationPoint(
            id = "coffee_north_end",
            name = "North End Coffee Roasters",
            city = "Dhaka",
            country = "Bangladesh",
            latitude = 23.7925,
            longitude = 90.4078,
            defaultZoom = 15f,
            category = LocationCategory.COFFEE,
            description = "Pioneer specialty artisanal coffee roastery in Gulshan serving single-origin pour-overs and freshly roasted beans.",
            trivia = "Hand-roasts ethically sourced beans from small-holder farms around the world in small batches daily.",
            elevationMeters = 16,
            imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=600&auto=format&fit=crop&q=80",
            plusCode = "QCR5+34 Dhaka",
            rating = 4.8
        ),
        LocationPoint(
            id = "coffee_cafe_de_flore",
            name = "Café de Flore",
            city = "Paris",
            country = "France",
            latitude = 48.8542,
            longitude = 2.3328,
            defaultZoom = 15f,
            category = LocationCategory.COFFEE,
            description = "One of the oldest and most celebrated coffeehouses in Paris, historic home to Jean-Paul Sartre and Simone de Beauvoir.",
            trivia = "Open since 1887 on the Boulevard Saint-Germain, preserving classic Art Deco mirrors and red mahogany booths.",
            elevationMeters = 35,
            imageUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=600&auto=format&fit=crop&q=80",
            plusCode = "V83M+M4 Paris",
            rating = 4.6
        ),
        LocationPoint(
            id = "coffee_blue_bottle",
            name = "Blue Bottle Coffee Ferry Building",
            city = "San Francisco",
            country = "United States",
            latitude = 37.7955,
            longitude = -122.3937,
            defaultZoom = 15f,
            category = LocationCategory.COFFEE,
            description = "Legendary waterfront third-wave coffee bar overlooking San Francisco Bay, known for New Orleans-style iced coffee.",
            trivia = "Named after Central Europe's first coffeehouse, The Blue Bottle, founded in Vienna in 1683.",
            elevationMeters = 5,
            imageUrl = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=600&auto=format&fit=crop&q=80",
            plusCode = "QHQ4+6G San Francisco, CA",
            rating = 4.7
        ),

        // Quick Category: Shops & Boutiques
        LocationPoint(
            id = "shop_aarong",
            name = "Aarong Flagship Store",
            city = "Dhaka",
            country = "Bangladesh",
            latitude = 23.7745,
            longitude = 90.3654,
            defaultZoom = 15f,
            category = LocationCategory.SHOPS,
            description = "Premier social enterprise fair-trade lifestyle department store showcasing handmade silks, handicrafts, and nakshi kantha.",
            trivia = "Supports over 65,000 rural artisans and handicraft producers across Bangladesh through BRAC.",
            elevationMeters = 15,
            imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=600&auto=format&fit=crop&q=80",
            plusCode = "Q9F8+R5 Dhaka",
            rating = 4.9
        ),
        LocationPoint(
            id = "shop_harrods",
            name = "Harrods Luxury Department Store",
            city = "London",
            country = "United Kingdom",
            latitude = 51.4994,
            longitude = -0.1633,
            defaultZoom = 15f,
            category = LocationCategory.SHOPS,
            description = "World-famous department store in Knightsbridge spanning 330 departments across over one million square feet.",
            trivia = "Motto 'Omnia Omnibus Ubique' — All Things for All People, Everywhere. Installed the UK's first moving staircase in 1898.",
            elevationMeters = 20,
            imageUrl = "https://images.unsplash.com/photo-1555529669-e69e7aa0ba9a?w=600&auto=format&fit=crop&q=80",
            plusCode = "FRXQ+QM London",
            rating = 4.8
        ),
        LocationPoint(
            id = "shop_fifth_ave",
            name = "Fifth Avenue Shopping District",
            city = "New York",
            country = "United States",
            latitude = 40.7619,
            longitude = -73.9740,
            defaultZoom = 15f,
            category = LocationCategory.SHOPS,
            description = "One of the most expensive and prestigious retail shopping corridors in the world, stretching through Midtown Manhattan.",
            trivia = "Home to iconic luxury boutiques, Tiffany & Co. flagship, Bergdorf Goodman, and Saks Fifth Avenue.",
            elevationMeters = 28,
            imageUrl = "https://images.unsplash.com/photo-1481437156560-3205f6a55735?w=600&auto=format&fit=crop&q=80",
            plusCode = "Q26G+Q2 New York",
            rating = 4.9
        ),

        // Quick Category: Supermarkets
        LocationPoint(
            id = "supermarket_agora",
            name = "Agora Supermarket",
            city = "Dhaka",
            country = "Bangladesh",
            latitude = 23.7928,
            longitude = 90.4182,
            defaultZoom = 15f,
            category = LocationCategory.SUPERMARKETS,
            description = "Pioneering retail supermarket chain in Bangladesh offering fresh produce, local spices, groceries, and bakery items.",
            trivia = "Opened in 2001 as the first organized modern supermarket chain in Bangladesh.",
            elevationMeters = 16,
            imageUrl = "https://images.unsplash.com/photo-1578916171728-46686eac8d58?w=600&auto=format&fit=crop&q=80",
            plusCode = "QCV9+47 Dhaka",
            rating = 4.6
        ),
        LocationPoint(
            id = "supermarket_whole_foods",
            name = "Whole Foods Market",
            city = "New York",
            country = "United States",
            latitude = 40.7685,
            longitude = -73.9829,
            defaultZoom = 15f,
            category = LocationCategory.SUPERMARKETS,
            description = "Vast organic supermarket at Columbus Circle featuring organic farm produce, prepared foods, and artisan cheeses.",
            trivia = "Enforces strict quality standards that prohibit over 300 artificial preservatives, colors, and hydrogenated fats.",
            elevationMeters = 24,
            imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=600&auto=format&fit=crop&q=80",
            plusCode = "Q298+CF New York",
            rating = 4.7
        ),
        LocationPoint(
            id = "supermarket_carrefour",
            name = "Carrefour Hypermarket",
            city = "Paris",
            country = "France",
            latitude = 48.8912,
            longitude = 2.2415,
            defaultZoom = 15f,
            category = LocationCategory.SUPERMARKETS,
            description = "Massive hypermarket carrying international groceries, fine French wines, charcuterie, and home appliances.",
            trivia = "Carrefour invented the hypermarket concept in 1963, uniting supermarket groceries with department store goods.",
            elevationMeters = 40,
            imageUrl = "https://images.unsplash.com/photo-1534723452862-4c874018d66d?w=600&auto=format&fit=crop&q=80",
            plusCode = "VRRW+F7 Courbevoie",
            rating = 4.5
        ),

        // Quick Category: Showrooms & Auto
        LocationPoint(
            id = "showroom_tesla",
            name = "Tesla Motors Experience Showroom",
            city = "Los Angeles",
            country = "United States",
            latitude = 34.0152,
            longitude = -118.4965,
            defaultZoom = 15f,
            category = LocationCategory.SHOWROOMS,
            description = "Interactive electric vehicle showroom featuring the Cybertruck, Model S Plaid, Full Self-Driving simulators, and Powerwalls.",
            trivia = "Pioneered direct-to-consumer auto sales inside high-end shopping destinations without traditional franchise dealerships.",
            elevationMeters = 38,
            imageUrl = "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=600&auto=format&fit=crop&q=80",
            plusCode = "2G83+39 Santa Monica, CA",
            rating = 4.8
        ),
        LocationPoint(
            id = "showroom_ferrari",
            name = "Ferrari World Experience Showroom",
            city = "Abu Dhabi",
            country = "United Arab Emirates",
            latitude = 24.4836,
            longitude = 54.6075,
            defaultZoom = 15f,
            category = LocationCategory.SHOWROOMS,
            description = "Spectacular supercar showroom and motorsport museum celebrating the Prancing Horse with iconic Formula 1 race cars.",
            trivia = "Housed beneath an iconic red roof spanning 200,000 square metres bearing the largest Ferrari logo ever created.",
            elevationMeters = 10,
            imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600&auto=format&fit=crop&q=80",
            plusCode = "FJMW+C2 Abu Dhabi",
            rating = 4.9
        ),
        LocationPoint(
            id = "showroom_bmw_welt",
            name = "BMW Welt Experience Showroom",
            city = "Munich",
            country = "Germany",
            latitude = 48.1770,
            longitude = 11.5562,
            defaultZoom = 15f,
            category = LocationCategory.SHOWROOMS,
            description = "Futuristic double-cone glass and steel architectural masterpiece showcasing BMW, MINI, Rolls-Royce, and Motorrad vehicles.",
            trivia = "Features an 800 kW rooftop photovoltaic plant and a spiraling ramp for VIP customer vehicle handovers.",
            elevationMeters = 512,
            imageUrl = "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600&auto=format&fit=crop&q=80",
            plusCode = "5HGV+RF Munich",
            rating = 4.9
        ),

        // World Wonders & Landmarks
        LocationPoint(
            id = "eiffel_tower",
            name = "Eiffel Tower",
            city = "Paris",
            country = "France",
            latitude = 48.8584,
            longitude = 2.2945,
            defaultZoom = 14f,
            category = LocationCategory.WONDERS,
            description = "Iconic 330-metre wrought-iron lattice tower on the Champ de Mars, the global symbol of France.",
            trivia = "Constructed in 1889 for the World's Fair, it was originally meant to stand for only 20 years.",
            elevationMeters = 330,
            imageUrl = "https://images.unsplash.com/photo-1511739001486-6bfe10ce785f?w=600&auto=format&fit=crop&q=80",
            plusCode = "V75W+8V Paris"
        ),
        LocationPoint(
            id = "statue_liberty",
            name = "Statue of Liberty",
            city = "New York",
            country = "United States",
            latitude = 40.6892,
            longitude = -74.0445,
            defaultZoom = 13.5f,
            category = LocationCategory.WONDERS,
            description = "Colossal neoclassical copper sculpture on Liberty Island in New York Harbor, dedicated in 1886.",
            trivia = "A gift from the people of France, designed by sculptor Frédéric-Auguste Bartholdi.",
            elevationMeters = 93,
            imageUrl = "https://images.unsplash.com/photo-1605130284535-11dd9eedc58a?w=600&auto=format&fit=crop&q=80",
            plusCode = "MXQ4+8X New York"
        ),
        LocationPoint(
            id = "taj_mahal",
            name = "Taj Mahal",
            city = "Agra",
            country = "India",
            latitude = 27.1751,
            longitude = 78.0421,
            defaultZoom = 14f,
            category = LocationCategory.WONDERS,
            description = "Ivory-white marble mausoleum on the south bank of the Yamuna river, UNESCO World Heritage site.",
            trivia = "Commissioned in 1631 by Mughal Emperor Shah Jahan to house the tomb of his favourite wife.",
            elevationMeters = 171,
            imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=600&auto=format&fit=crop&q=80",
            plusCode = "52GW+2R Agra"
        ),
        LocationPoint(
            id = "colosseum",
            name = "Colosseum",
            city = "Rome",
            country = "Italy",
            latitude = 41.8902,
            longitude = 12.4922,
            defaultZoom = 14f,
            category = LocationCategory.WONDERS,
            description = "Largest ancient amphitheatre ever built, constructed in 70-80 AD in the heart of Rome.",
            trivia = "Could hold an estimated 50,000 to 80,000 spectators for gladiatorial spectacles.",
            elevationMeters = 48,
            imageUrl = "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=600&auto=format&fit=crop&q=80",
            plusCode = "VF2R+3R Rome"
        ),
        LocationPoint(
            id = "pyramids_giza",
            name = "Great Pyramids of Giza",
            city = "Cairo",
            country = "Egypt",
            latitude = 29.9792,
            longitude = 31.1342,
            defaultZoom = 13f,
            category = LocationCategory.WONDERS,
            description = "Oldest of the Seven Wonders of the Ancient World, standing on the Giza plateau for 4,500 years.",
            trivia = "Built as royal tombs for pharaohs Khufu, Khafre, and Menkaure around 2560 BC.",
            elevationMeters = 138,
            imageUrl = "https://images.unsplash.com/photo-1503177119275-0aa32b3a9368?w=600&auto=format&fit=crop&q=80",
            plusCode = "XHHF+MM Al Giza"
        ),
        LocationPoint(
            id = "burj_khalifa",
            name = "Burj Khalifa",
            city = "Dubai",
            country = "United Arab Emirates",
            latitude = 25.1972,
            longitude = 55.2744,
            defaultZoom = 14f,
            category = LocationCategory.CITIES,
            description = "The tallest structure and building in the world since its topping out in 2009, soaring 828 metres.",
            trivia = "Features the world's highest outdoor observation deck and world's longest elevator travel distance.",
            elevationMeters = 828,
            imageUrl = "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?w=600&auto=format&fit=crop&q=80",
            plusCode = "5798+9Q Dubai"
        ),
        LocationPoint(
            id = "tokyo_shibuya",
            name = "Shibuya & Tokyo Tower",
            city = "Tokyo",
            country = "Japan",
            latitude = 35.6586,
            longitude = 139.7454,
            defaultZoom = 13f,
            category = LocationCategory.CITIES,
            description = "Vibrant metropolis blending futuristic neon skyscrapers, serene temples, and cutting-edge transit.",
            trivia = "The Tokyo Tower was inspired by the Eiffel Tower and is painted white and international orange for aviation safety.",
            elevationMeters = 333,
            imageUrl = "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=600&auto=format&fit=crop&q=80",
            plusCode = "MP5W+74 Minato City"
        )
    )

    fun calculateSampleRoute(from: LocationPoint, to: LocationPoint): NavigationRoute {
        val latDiff = Math.toRadians(to.latitude - from.latitude)
        val lonDiff = Math.toRadians(to.longitude - from.longitude)
        val a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(from.latitude)) * Math.cos(Math.toRadians(to.latitude)) *
                Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val earthRadiusKm = 6371.0
        val distKm = earthRadiusKm * c
        val flightHours = distKm / 850.0

        return NavigationRoute(
            title = "${from.city} -> ${to.city}",
            origin = from,
            destination = to,
            distanceKm = distKm,
            flightHours = flightHours,
            waypointsCount = 12
        )
    }
}
