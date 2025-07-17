package com.zeezaglobal.posresturant.Application

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zeezaglobal.posresturant.Database.POSDatabase
import com.zeezaglobal.posresturant.Entities.Group
import com.zeezaglobal.posresturant.Entities.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class POSApp : Application() {

    lateinit var database: POSDatabase

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            POSDatabase::class.java,
            "my-database"
        )
            .addCallback(DatabaseCallback())
            .build()
    }

    private inner class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                populateDatabase(database)
            }
        }
    }

    private suspend fun populateDatabase(database: POSDatabase) {
        val groupDao = database.groupDao()
        val itemDao = database.itemDao()

        // Prepopulate groups
        val groups = listOf(
            Group(groupName = "HOT BEV"),
            Group(groupName = "HOT BEV SPL"),
            Group(groupName = "ICED BEV"),
            Group(groupName = "ICED BEV SPL"),
            Group(groupName = "COLD COFFEE"),
            Group(groupName = "COFFEE FRAPPE"),
            Group(groupName = "AFFAGATO"),
            Group(groupName = "MOJITTO"),
            Group(groupName = "CRUSHERS"),
            Group(groupName = "NONCOFFEE FRAPPE"),
            Group(groupName = "POPCORN"),
            Group(groupName = "POPSICLE"),
            Group(groupName = "SNACKS"),
            Group(groupName = "TEA"),
            Group(groupName = "MOKTAIL")
        )
        val groupIds = groups.map { groupDao.insertGroup(it) }

        // Prepopulate items
        val items = listOf(


            Item(groupId = 1, itemName = "Cranberry Hot Chocolate", itemDescription = "4 OZ", itemPrice = 120.0),
            Item(groupId = 1, itemName = "Classic Cinnamon", itemDescription = "4 OZ", itemPrice = 40.0),
            Item(groupId = 1, itemName = "Classic Cinnamon", itemDescription = "7 OZ", itemPrice = 70.0),
            Item(groupId = 1, itemName = "DAVIDOFF", itemDescription = "4 OZ", itemPrice = 40.0),
            Item(groupId = 1, itemName = "Espresso", itemDescription = "4 OZ", itemPrice = 50.0),
            Item(groupId = 1, itemName = "Babyccino", itemDescription = "4 OZ", itemPrice = 70.0),
            Item(groupId = 1, itemName = "Piccolo Latte", itemDescription = "4 OZ", itemPrice = 70.0),
            Item(groupId = 1, itemName = "Americano", itemDescription = "7 OZ", itemPrice = 100.0),
            Item(groupId = 1, itemName = "Cappuccino", itemDescription = "7 OZ", itemPrice = 130.0),
            Item(groupId = 1, itemName = "Cappuccino", itemDescription = "8 OZ", itemPrice = 150.0),
            Item(groupId = 1, itemName = "Flat White", itemDescription = "8 OZ", itemPrice = 140.0),
            Item(groupId = 1, itemName = "Café Latte", itemDescription = "7 OZ", itemPrice = 140.0),
            Item(groupId = 1, itemName = "Café Latte", itemDescription = "8 OZ", itemPrice = 160.0),
            Item(groupId = 1, itemName = "Café Bombon", itemDescription = "4 OZ", itemPrice = 80.0),
            Item(groupId = 1, itemName = "Hazelnut Latte", itemDescription = "7 OZ", itemPrice = 150.0),
            Item(groupId = 1, itemName = "Hazelnut Latte", itemDescription = "8 OZ", itemPrice = 170.0),
            Item(groupId = 1, itemName = "Irish Latte", itemDescription = "7 OZ", itemPrice = 150.0),
            Item(groupId = 1, itemName = "Irish Latte", itemDescription = "8 OZ", itemPrice = 170.0),
            Item(groupId = 1, itemName = "Caramel Latte", itemDescription = "7 OZ", itemPrice = 150.0),
            Item(groupId = 1, itemName = "Caramel Latte", itemDescription = "8 OZ", itemPrice = 170.0),
            Item(groupId = 1, itemName = "Butterscotch Latte", itemDescription = "7 OZ", itemPrice = 150.0),
            Item(groupId = 1, itemName = "Butterscotch Latte", itemDescription = "8 OZ", itemPrice = 170.0),
            Item(groupId = 1, itemName = "Vanilla Latte", itemDescription = "7 OZ", itemPrice = 150.0),
            Item(groupId = 1, itemName = "Vanilla Latte", itemDescription = "8 OZ", itemPrice = 170.0),
            Item(groupId = 1, itemName = "Mocha Coffee", itemDescription = "7 OZ", itemPrice = 190.0),
            Item(groupId = 1, itemName = "Premium Hot Chocolate", itemDescription = "4 OZ", itemPrice = 100.0),
            Item(groupId = 1, itemName = "Bounty Hot Chocolate", itemDescription = "4 OZ", itemPrice = 120.0),

            Item(groupId = 3, itemName = "Iced Coffee", itemDescription = "8 OZ", itemPrice = 140.0),
            Item(groupId = 3, itemName = "Iced Espresso", itemDescription = "4 OZ", itemPrice = 80.0),
            Item(groupId = 3, itemName = "Iced Americano", itemDescription = "8 OZ", itemPrice = 110.0),
            Item(groupId = 3, itemName = "Iced Hazelnut Latte", itemDescription = "8 OZ", itemPrice = 150.0),
            Item(groupId = 3, itemName = "Iced Irish Latte", itemDescription = "8 OZ", itemPrice = 150.0),
            Item(groupId = 3, itemName = "Iced Caramel Latte", itemDescription = "8 OZ", itemPrice = 150.0),
            Item(groupId = 3, itemName = "Iced Vanilla Latte", itemDescription = "8 OZ", itemPrice = 150.0)
,
            Item(groupId = 4, itemName = "Iced Vietnamese Coffee", itemDescription = "4 OZ", itemPrice = 90.0),
            Item(groupId = 4, itemName = "Americola", itemDescription = "8 OZ", itemPrice = 100.0),
            Item(groupId = 4, itemName = "Cranberry Coffee", itemDescription = "350 ml", itemPrice = 120.0),
            Item(groupId = 4, itemName = "Orange Sparkle Espresso", itemDescription = "350 ml", itemPrice = 130.0)
,
            Item(groupId = 5, itemName = "Cold Coffee", itemDescription = "350 ml", itemPrice = 160.0),
            Item(groupId = 5, itemName = "hazelnut CC", itemDescription = "350 ml", itemPrice = 170.0),
            Item(groupId = 5, itemName = "Irish cc", itemDescription = "350 ml", itemPrice = 170.0),
            Item(groupId = 5, itemName = "Vanilla cc", itemDescription = "350 ml", itemPrice = 170.0),
            Item(groupId = 5, itemName = "Caramel Cc", itemDescription = "350 ml", itemPrice = 170.0),
            Item(groupId = 5, itemName = "Butter scoth Cc", itemDescription = "350 ml", itemPrice = 170.0),


            Item(groupId = 6, itemName = "chocolate frappe", itemDescription = "350 ml", itemPrice = 190.0),
            Item(groupId = 6, itemName = "Hazelnut Frappe", itemDescription = "350 ml", itemPrice = 190.0),
            Item(groupId = 6, itemName = "Vanilla Frappe", itemDescription = "350 ml", itemPrice = 190.0),
            Item(groupId = 6, itemName = "Caramel Frappe", itemDescription = "350 ml", itemPrice = 190.0),
            Item(groupId = 6, itemName = "Brownie Frappe", itemDescription = "350 ml", itemPrice = 210.0),


            Item(groupId = 7, itemName = "Vanilla Affogato", itemDescription = "SINGLE SCOOP", itemPrice = 110.0),
            Item(groupId = 7, itemName = "Chocolate Affogato", itemDescription = "SINGLE SCOOP", itemPrice = 120.0)
,
            Item(groupId = 8, itemName = "Passionfruit", itemDescription = "350 ml", itemPrice = 99.0),
            Item(groupId = 8, itemName = "Blue Ocean", itemDescription = "350 ml", itemPrice = 99.0),
            Item(groupId = 8, itemName = "Green Apple", itemDescription = "350 ml", itemPrice = 99.0),
            Item(groupId = 8, itemName = "Strawberry", itemDescription = "350 ml", itemPrice = 99.0),
            Item(groupId = 8, itemName = "Watermelon", itemDescription = "350 ml", itemPrice = 99.0),

            Item(groupId = 8, itemName = "Tropical Mocktail", itemDescription = "8 OZ", itemPrice = 120.0)
,
            Item(groupId = 9, itemName = "Passionfruit Crushers", itemDescription = "350 ml", itemPrice = 109.0),
            Item(groupId = 9, itemName = "Watermelon Crushers", itemDescription = "350 ml", itemPrice = 109.0),
            Item(groupId = 9, itemName = "Green apple Crushers", itemDescription = "350 ml", itemPrice = 109.0),
            Item(groupId = 9, itemName = "Popsicle Jamun crushers", itemDescription = "350 ml", itemPrice = 150.0),
            Item(groupId = 9, itemName = "Popsicle Guvava crushers", itemDescription = "350 ml", itemPrice = 150.0),
            Item(groupId = 9, itemName = "Popsicle Grape crushers", itemDescription = "350 ml", itemPrice = 150.0),


            Item(groupId = 10, itemName = "mango kesar shake", itemDescription = "350 ml", itemPrice = 195.0),
            Item(groupId = 10, itemName = "butterscoch frappe", itemDescription = "350 ml", itemPrice = 195.0),
            Item(groupId = 10, itemName = "crunchy biscoff shake", itemDescription = "350 ml", itemPrice = 210.0),
            Item(groupId = 10, itemName = "Ferrero Frappe", itemDescription = "350 ml", itemPrice = 210.0),

            Item(groupId = 11, itemName = "Salty Waves pops", itemDescription = "30gm", itemPrice = 50.0),
            Item(groupId = 11, itemName = "Cheddar Cheese pops", itemDescription = "30gm", itemPrice = 60.0),
            Item(groupId = 11, itemName = "Caramel Crunch pops", itemDescription = "30gm", itemPrice = 60.0),


            Item(groupId = 12, itemName = "Juicy Jamun Bliss", itemDescription = "popsicle", itemPrice = 45.0),
            Item(groupId = 12, itemName = "Guava Glow Stick", itemDescription = "popsicle", itemPrice = 45.0),
            Item(groupId = 12, itemName = "Grape Frost Burst", itemDescription = "popsicle", itemPrice = 45.0),
            Item(groupId = 12, itemName = "Cotton Candy", itemDescription = "popsicle", itemPrice = 35.0),
            Item(groupId = 12, itemName = "Lotus Biscoff", itemDescription = "popsicle", itemPrice = 35.0),
            Item(groupId = 12, itemName = "Black Current", itemDescription = "popsicle", itemPrice = 35.0),
            Item(groupId = 12, itemName = "Spanish Delight", itemDescription = "popsicle", itemPrice = 35.0),
            Item(groupId = 12, itemName = "Tender Coconut", itemDescription = "popsicle", itemPrice = 35.0),


            Item(groupId = 13, itemName = "Bread & Cheese Sanwitch", itemDescription = "SNACKS", itemPrice = 80.0),
            Item(groupId = 13, itemName = "Chocolate Brownie", itemDescription = "SNACKS", itemPrice = 120.0),
            Item(groupId = 13, itemName = "Butter Bun", itemDescription = "SNACKS", itemPrice = 35.0),
            Item(groupId = 13, itemName = "Cookie", itemDescription = "SNACKS", itemPrice = 15.0),
            Item(groupId = 13, itemName = "Normal Brownie", itemDescription = "SNACKS", itemPrice = 90.0),
            Item(groupId = 13, itemName = "Vannila Scoop", itemDescription = "SNACKS", itemPrice = 40.0),
            Item(groupId = 13, itemName = "Chocolate Scoop", itemDescription = "SNACKS", itemPrice = 50.0),


            Item(groupId = 14, itemName = "Lemon Tea", itemDescription = "7 OZ", itemPrice = 40.0),
            Item(groupId = 14, itemName = "Green Tea with Honey", itemDescription = "7 OZ", itemPrice = 60.0),
            Item(groupId = 14, itemName = "Black Tea with Ginger", itemDescription = "7 OZ", itemPrice = 40.0),


            Item(groupId = 15, itemName = "Tropical Rose Mocktail", itemDescription = "350 ml", itemPrice = 120.0),
            Item(groupId = 15, itemName = "Blueberry Mocktail", itemDescription = "350 ml", itemPrice = 120.0),
            Item(groupId = 15, itemName = "Mexican Mocktail", itemDescription = "350 ml", itemPrice = 120.0),




            )
        items.forEach { itemDao.insertItem(it) }
    }
}
