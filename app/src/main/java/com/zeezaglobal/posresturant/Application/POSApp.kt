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
            Group(groupName = "SNACKS")
        )
        val groupIds = groups.map { groupDao.insertGroup(it) }

        // Prepopulate items
        val items = listOf(
            Item(groupId = 1, itemName = "Espresso 4 OZ", itemDescription = "4 OZ", itemPrice = 40.0),
            Item(groupId = 1, itemName = "Babyccino 4OZ", itemDescription = "4 OZ", itemPrice = 60.0),
            Item(groupId = 1, itemName = "Piccolo Latte 4OZ", itemDescription = "4OZ", itemPrice = 70.0),
            Item(groupId = 1, itemName = "Americano 7OZ", itemDescription = "7OZ", itemPrice = 90.0),
            Item(groupId = 1, itemName = "Cappuccino 7OZ", itemDescription = "7OZ", itemPrice = 120.0),
            Item(groupId = 1, itemName = "Cappuccino 8OZ", itemDescription = "8OZ", itemPrice = 140.0),
            Item(groupId = 1, itemName = "Flat White 8OZ", itemDescription = "8 OZ", itemPrice = 130.0),
            Item(groupId = 1, itemName = "Café Latte 7OZ", itemDescription = "7OZ", itemPrice = 120.0),
            Item(groupId = 1, itemName = "Café Latte 8OZ", itemDescription = "8OZ", itemPrice = 140.0),
            Item(groupId = 2, itemName = "Café bombon 4OZ", itemDescription = "4OZ", itemPrice = 80.0),
            Item(groupId = 1, itemName = "Hazelnut latte 7OZ", itemDescription = "7OZ", itemPrice = 140.0),
            Item(groupId = 1, itemName = "Hazelnut latte 8OZ", itemDescription = "8OZ", itemPrice = 160.0),
            Item(groupId = 1, itemName = "Irish Latte 7OZ", itemDescription = "7OZ", itemPrice = 140.0),
            Item(groupId = 1, itemName = "Irish Latte 8OZ", itemDescription = "8OZ", itemPrice = 160.0),
            Item(groupId = 1, itemName = "Caramel Latte 7OZ", itemDescription = "7OZ", itemPrice = 140.0),
            Item(groupId = 1, itemName = "Caramel Latte 8OZ", itemDescription = "8OZ", itemPrice = 160.0),
            Item(groupId = 1, itemName = "Butterscotch Latte 7OZ", itemDescription = "7OZ", itemPrice = 140.0),
            Item(groupId = 1, itemName = "Butterscotch Latte 8 OZ", itemDescription = "8OZ", itemPrice = 160.0),
            Item(groupId = 1, itemName = "Vanilla Latte 7OZ", itemDescription = "7OZ", itemPrice = 140.0),
            Item(groupId = 1, itemName = "Vanilla Latte 8 OZ", itemDescription = "8OZ", itemPrice = 160.0),
            Item(groupId = 1, itemName = "Mocha Coffee 7OZ", itemDescription = "7OZ", itemPrice = 180.0),
            Item(groupId = 1, itemName = "Hot Chocolate", itemDescription = "4OZ", itemPrice = 79.0),
            Item(groupId = 1, itemName = "Premium Hot Chocolate", itemDescription = "4OZ", itemPrice = 99.0),
            Item(groupId = 1, itemName = "Lemon Tea", itemDescription = "7OZ", itemPrice = 90.0),
            Item(groupId = 1, itemName = "Green Tea with Honey", itemDescription = "7OZ", itemPrice = 90.0),
            Item(groupId = 1, itemName = "Black Tea with Ginger", itemDescription = "7OZ", itemPrice = 90.0),
            Item(groupId = 3, itemName = "Iced Coffee", itemDescription = "8OZ", itemPrice = 130.0),
            Item(groupId = 3, itemName = "Iced Espresso 4OZ", itemDescription = "4OZ", itemPrice = 55.0),
            Item(groupId = 3, itemName = "Iced Americano", itemDescription = "8OZ", itemPrice = 95.0),
            Item(groupId = 3, itemName = "Iced Hazelnut Latte", itemDescription = "8OZ", itemPrice = 140.0),
            Item(groupId = 3, itemName = "Iced Irish Latte", itemDescription = "8OZ", itemPrice = 140.0),
            Item(groupId = 3, itemName = "Iced Caramel Latte", itemDescription = "8OZ", itemPrice = 140.0),
            Item(groupId = 3, itemName = "Iced Vanilla Latte", itemDescription = "8OZ", itemPrice = 140.0),
            Item(groupId = 4, itemName = "Iced Vietnamese Coffee 4OZ", itemDescription = "4OZ", itemPrice = 80.0),
            Item(groupId = 4, itemName = "Americola", itemDescription = "8OZ", itemPrice = 100.0),
            Item(groupId = 5, itemName = "Cold Coffee", itemDescription = "8OZ", itemPrice = 150.0),
            Item(groupId = 5, itemName = "Hazelnut CC", itemDescription = "8OZ", itemPrice = 160.0),
            Item(groupId = 5, itemName = "Irish CC", itemDescription = "8OZ", itemPrice = 160.0),
            Item(groupId = 5, itemName = "Vanilla CC", itemDescription = "8OZ", itemPrice = 160.0),
            Item(groupId = 5, itemName = "Caramel CC", itemDescription = "8OZ", itemPrice = 160.0),
            Item(groupId = 6, itemName = "Chocolate Frappe", itemDescription = "8OZ", itemPrice = 180.0),
            Item(groupId = 6, itemName = "Hazelnut Frappe", itemDescription = "8OZ", itemPrice = 180.0),
            Item(groupId = 6, itemName = "Vanilla Frappe", itemDescription = "8OZ", itemPrice = 180.0),
            Item(groupId = 6, itemName = "Caramel Frappe", itemDescription = "8OZ", itemPrice = 180.0),
            Item(groupId = 7, itemName = "Vanilla Affogato", itemDescription = "SINGLE SCOOP", itemPrice = 99.0),
            Item(groupId = 7, itemName = "Chocolate Affogato", itemDescription = "SINGLE SCOOP", itemPrice = 199.0),
            Item(groupId = 8, itemName = "Passionfruit Mojitto", itemDescription = "8OZ", itemPrice = 99.0),
            Item(groupId = 8, itemName = "Blue Ocean Mojitto", itemDescription = "8OZ", itemPrice = 99.0),
            Item(groupId = 8, itemName = "Green Apple Mojitto", itemDescription = "8OZ", itemPrice = 99.0),
            Item(groupId = 8, itemName = "Strawberry Mojitto", itemDescription = "8OZ", itemPrice = 99.0),
            Item(groupId = 8, itemName = "Watermelon Mojitto", itemDescription = "8OZ", itemPrice = 99.0),
            Item(groupId = 9, itemName = "Passionfruit Crushers", itemDescription = "8OZ", itemPrice = 109.0),
            Item(groupId = 9, itemName = "Watermelon Crushers", itemDescription = "8OZ", itemPrice = 109.0),
            Item(groupId = 9, itemName = "Green Apple Crushers", itemDescription = "8OZ", itemPrice = 109.0),
            Item(groupId = 10, itemName = "Mango Kesar Shake", itemDescription = "8OZ", itemPrice = 200.0),
            Item(groupId = 10, itemName = "Butterscotch Frappe", itemDescription = "8OZ", itemPrice = 200.0),
            Item(groupId = 10, itemName = "Crunchy Biscoff Shake", itemDescription = "8OZ", itemPrice = 200.0),
            Item(groupId = 11, itemName = "Salty Waves Pops", itemDescription = "30GM", itemPrice = 50.0),
            Item(groupId = 11, itemName = "Cheddar Cheese Pops", itemDescription = "30GM", itemPrice = 60.0),
            Item(groupId = 11, itemName = "Caramel Crunch Pops", itemDescription = "30GM", itemPrice = 60.0),
            Item(groupId = 12, itemName = "Juicy Jamun Bliss", itemDescription = "POPSICLE", itemPrice = 45.0),
            Item(groupId = 12, itemName = "Guava Glow Stick", itemDescription = "POPSICLE", itemPrice = 45.0),
            Item(groupId = 12, itemName = "Grape Frost Burst", itemDescription = "POPSICLE", itemPrice = 45.0),
            Item(groupId = 12, itemName = "Cotton Candy", itemDescription = "POPSICLE", itemPrice = 35.0),
            Item(groupId = 12, itemName = "Lotus Biscoff", itemDescription = "POPSICLE", itemPrice = 35.0),
            Item(groupId = 12, itemName = "Black Current", itemDescription = "POPSICLE", itemPrice = 35.0),
            Item(groupId = 12, itemName = "Spanish Delight", itemDescription = "POPSICLE", itemPrice = 30.0),
            Item(groupId = 12, itemName = "Tender Coconut", itemDescription = "POPSICLE", itemPrice = 30.0),
            Item(groupId = 13, itemName = "Chocolate Brownie", itemDescription = "SNACKS", itemPrice = 110.0),
            Item(groupId = 13, itemName = "Chocolate Croissants", itemDescription = "SNACKS", itemPrice = 80.0),
            Item(groupId = 13, itemName = "Chocolate Muffins", itemDescription = "SNACKS", itemPrice = 40.0),
            Item(groupId = 13, itemName = "Butter Bun", itemDescription = "SNACKS", itemPrice = 40.0),
            Item(groupId = 13, itemName = "Chocolate Bun", itemDescription = "SNACKS", itemPrice = 45.0),
            Item(groupId = 13, itemName = "Chocochip Cookie", itemDescription = "SNACKS", itemPrice = 12.0),
            Item(groupId = 13, itemName = "Butter Cookie", itemDescription = "SNACKS", itemPrice = 12.0),
            Item(groupId = 13, itemName = "Coffee Cookie", itemDescription = "SNACKS", itemPrice = 12.0),
            Item(groupId = 13, itemName = "VANNILA SCOOP", itemDescription = "ICE", itemPrice = 40.0),
            Item(groupId = 13, itemName = "CHOCO SCOOP", itemDescription = "ICE", itemPrice = 50.0)

        )
        items.forEach { itemDao.insertItem(it) }
    }
}
