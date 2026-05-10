import 'package:path/path.dart' as p;
import 'package:sqflite/sqflite.dart';

class DatabaseService {
  Database? _database;

  Future<Database> get database async {
    if (_database != null) {
      return _database!;
    }

    final dbPath = await getDatabasesPath();
    final path = p.join(dbPath, 'aurora_drive.db');

    _database = await openDatabase(
      path,
      version: 1,
      onCreate: (db, version) async {
        await db.execute('''
          CREATE TABLE bookings(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            carId TEXT NOT NULL,
            carName TEXT NOT NULL,
            durationType TEXT NOT NULL,
            duration INTEGER NOT NULL,
            totalPrice INTEGER NOT NULL,
            bookedAt TEXT NOT NULL,
            status TEXT NOT NULL
          )
        ''');
      },
    );

    return _database!;
  }
}
