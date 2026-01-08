# Image Upload & Amazon-Style UI - Complete Implementation
---

## Technical Details

### Image Storage
- **Location**: `images/` folder in project root
- **Auto-creation**: Folder created on first image upload if missing
- **Naming**: `[System.currentTimeMillis()]_[original_filename]`
- **Uniqueness**: Guaranteed by timestamp (millisecond precision)

### Image Display
- **Scaling**: 210x210 pixels for product cards
- **Quality**: Image.SCALE_SMOOTH for smooth rendering
- **Fallback**: 📷 emoji if file doesn't exist
- **Performance**: Images scaled at display time, not stored scaled

### Database
- **Column**: `imageUrl VARCHAR(500)`
- **Content**: Relative path like `images/1765810267119_nike.jpg`
- **Nullable**: YES (handles products without images)
- **Auto-created**: Yes, on application startup

### UI Components
- **JFileChooser**: Windows native file picker (not custom Java dialog)
- **Filters**: jpg, jpeg, png, gif, bmp
- **Layout**: GridLayout(0, 3, 15, 15) = 3 columns, 15px gaps
- **Split Pane**: JSplitPane with 70/30 width distribution

---

## Compilation & Running

### Compile
```powershell
cd <project-root>
javac -encoding UTF-8 -d bin -cp "lib/*" src/Main.java src/db/*.java src/model/*.java src/dao/*.java src/ui/*.java
```

### Run
```powershell
java -cp "bin;lib/*" src.Main
```

---

## Testing Checklist

- [ ] Application starts without errors
- [ ] Admin can click " Browse" button
- [ ] File picker opens (Windows native dialog)
- [ ] Can select image file (jpg, png, etc)
- [ ] After selection, status shows "✓ filename"
- [ ] Can add product with image
- [ ] Check database: `SELECT * FROM products WHERE id=[new_product_id]`
  - Verify: imageUrl column has path like `images/1765810267119_filename`
- [ ] Check file system: `images/` folder has copied image file
- [ ] Customer logs in and browses products
- [ ] Product images display in grid (not 📷 emoji)
- [ ] Product cards have: Image, name, ₱price, stock, Add to Cart button
- [ ] Hover effect works (blue border)
- [ ] Can add products to cart
- [ ] Cart shows totals correctly
- [ ] Can place order successfully

---

---

## Notes

1. **Image Paths**: Stored as relative paths (e.g., `images/file.jpg`) for portability
2. **File Permissions**: Ensure `images/` folder is writable
3. **Filename Conflicts**: Timestamp ensures no overwrites
4. **Database Columns**: Auto-created if missing (no manual schema changes needed)
5. **Backward Compatibility**: Old products without images still display (show 📷 emoji)
6. **Windows Native Picker**: Uses JFileChooser which opens actual Windows file dialog

---

**Application is ready to use!**
