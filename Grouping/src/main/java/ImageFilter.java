import java.io.File;

class ImageFilter{
    private String GIF = "gif";
    private String PNG = "png";
    private String JPG = "jpg";
    private String BMP = "bmp";
    private String JPEG = "jpeg";
    public boolean accept(File file) {
        if(file != null) {
            if(file.isDirectory())
                return false;
            String extension = getExtension(file);
            return extension != null && isSupported(extension);
        }
        return false;
    }
    private String getExtension(File file) {
        if(file != null) {
            String filename = file.getName();
            int dot = filename.lastIndexOf('.');
            if(dot > 0 && dot < filename.length()-1)
                return filename.substring(dot+1).toLowerCase();
        }
        return null;
    }
    private boolean isSupported(String ext) {
        return ext.equalsIgnoreCase(GIF) || ext.equalsIgnoreCase(PNG) ||
                ext.equalsIgnoreCase(JPG) || ext.equalsIgnoreCase(BMP) ||
                ext.equalsIgnoreCase(JPEG);
    }
}