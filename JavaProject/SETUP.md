# Quick Setup Guide

## Step-by-Step Installation

### 1. Database Setup (5 minutes)

1. **Start MySQL Server**
   ```bash
   # Windows: Start MySQL service
   net start MySQL80
   
   # Or use MySQL Workbench / XAMPP
   ```

2. **Create Database**
   ```sql
   CREATE DATABASE is2bdb;
   ```

3. **Import Schema**
   - Open `database_schema.sql` in MySQL Workbench
   - Or run: `mysql -u root -p is2bdb < database_schema.sql`
   - This creates all tables and loads sample data

### 2. Configure Application (2 minutes)

1. **Update Database Password**
   
   Edit `src/db/DatabaseConnection.java`:
   ```java
   private static final String PASSWORD = "your_mysql_password";
   ```

2. **Add MySQL Connector**
   - Download from: https://dev.mysql.com/downloads/connector/j/
   - Extract and copy JAR file to `lib/` folder
   - Or use the one already in `lib/` if present

### 3. Run Application

#### Option A: Using Command Line

**Windows (PowerShell):**
```powershell
# Compile
javac -cp ".;lib/*" -d bin src/**/*.java src/*.java

# Run
java -cp "bin;lib/*" src.Main
```

**Mac/Linux:**
```bash
# Compile
javac -cp ".:lib/*" -d bin src/**/*.java src/*.java

# Run
java -cp "bin:lib/*" src.Main
```

#### Option B: Using VS Code

1. Install "Extension Pack for Java"
2. Open project folder
3. Press F5 or click Run → Run Without Debugging
4. If classpath errors, add `lib/*.jar` to referenced libraries

#### Option C: Using Eclipse

1. File → Import → Existing Projects into Workspace
2. Right-click project → Build Path → Add External JARs
3. Select MySQL Connector JAR from `lib/`
4. Run `Main.java`

#### Option D: Using IntelliJ IDEA

1. Open project folder
2. File → Project Structure → Libraries → Add JAR
3. Select MySQL Connector JAR from `lib/`
4. Run `Main.java`

### 4. Login

**Admin Access:**
- Email: `admin@ecommerce.com`
- Password: `admin123`

**Customer Access:**
- Email: `john@example.com`
- Password: `user123`

Or create new account using "Register" button.

---

## Verification Checklist

✅ MySQL Server is running
✅ Database `is2bdb` exists
✅ Tables created (8 tables total)
✅ Sample data loaded
✅ MySQL Connector JAR in `lib/` folder
✅ Database password updated in code
✅ Application compiles without errors
✅ Login screen appears

---

## Common Issues & Fixes

### Issue: "Access denied for user 'root'"
**Fix:** Update password in `DatabaseConnection.java`

### Issue: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Fix:** Add MySQL Connector JAR to classpath

### Issue: "Table 'is2bdb.users' doesn't exist"
**Fix:** Run `database_schema.sql` to create tables

### Issue: "Communications link failure"
**Fix:** Ensure MySQL server is running

---

## Quick Test

After login as admin:
1. Go to Products → See sample products
2. Go to Dashboard → See statistics
3. Go to Users → See registered users
4. Try adding a new product

After login as customer:
1. Browse products
2. Add item to cart
3. View cart
4. Place order

---

## File Checklist

Ensure these files exist:
```
✅ src/Main.java
✅ src/db/DatabaseConnection.java
✅ src/model/ (8 model files)
✅ src/dao/ (6 DAO files)
✅ src/ui/ (9 UI files)
✅ database_schema.sql
✅ lib/mysql-connector-java.jar
✅ README.md
```

---

## Next Steps

1. ✅ Complete setup above
2. 📖 Read full README.md for details
3. 🧪 Test all features
4. 🎨 Customize as needed
5. 📝 Document your changes

---

**Need Help?**
- Check troubleshooting in README.md
- Verify database connection
- Review error messages
- Test with sample accounts

**Ready to submit by:** December 9, 2025
