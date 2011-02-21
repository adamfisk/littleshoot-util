package org.littleshoot.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for determining the type of resources.
 * 
 * TODO: Compressed types like zip are weird because they could be anything.  
 * We should put them in documents, but have a separate category for type that
 * are OS-specific, like .sit. 
 */
public class ResourceTypeTranslatorImpl implements ResourceTypeTranslator
    {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    private final Map<String, String> m_types = 
        new ConcurrentHashMap<String, String>();
    
    private final String[] DOCUMENTS =
        {
        "html", "htm", "xhtml", "mht", "mhtml", "xml",
        "txt", "ans", "asc", "diz", "eml",
        "pdf", "ps", "epsf", "dvi", 
        "rtf", "wri", "doc", "mcw", "wps",
        "xls", "wk1", "dif", "csv", "ppt", "tsv",
        "hlp", "chm", "lit", 
        "tex", "texi", "latex", "info", "man",
        "wp", "wpd", "wp5", "wk3", "wk4", "shw", 
        "sdd", "sdw", "sdp", "sdc",
        "sxd", "sxw", "sxp", "sxc",
        "abw", "kwd", "js", "java", "cpp", "c", "py", "php", "ruby",
        "pps", // PowerPoint show
        "dll",
        "jhtml", // Java in html
        "mmap", // mind mapping document
        "dat", // data file
        "url", // link
        "bash",
        "sh",
        "csh",
        "pl",
        };
    
    private final String[] OSX_APPLICATIONS = 
        {
        "dmg", "pkg"
        };
    
    private final String[] LINUX_APPLICATIONS = 
        {
        "mdb",  "awk", 
        "rpm", "deb",  "z", "zoo", "tar", 
        "taz", "shar", "hqx", "7z", 
        };
    
    
    private final String[] WINDOWS_APPLICATIONS = 
        {
        "exe", "cab", "msi", "msp",
        "arj", "ace", 
        "nsi",  // Nullsoft installer.
        };   
    
    private final String[] AUDIO =
        {
        "mp3", "mpa", "mp1", "mpga", "mp2", 
        "ra", "rm", "ram", "rmj",
        "wma", "wav", "m4a", "m4p",
        "lqt", "ogg", "med",
        "aif", "aiff", "aifc",
        "au", "snd", "s3m", "aud", 
        "mid", "midi", "rmi", "mod", "kar",
        "ac3", "shn", "fla", "flac", "cda", 
        "mka", 
        };
    
    private final String[] VIDEO =
        {
        "mpg", "mpeg", "mpe", "mng", "mpv", "m1v",
        "vob", "mpv2", "mp2v", "m2p", "m2v", "m4v", "mpgv", 
        "vcd", "mp4", "dv", "dvd", "div", "divx", "dvx",
        "smi", "smil", "rv", "rmm", "rmvb", 
        "avi", "asf", "asx", "wmv", "qt", "mov",
        "fli", "flc", "flx", "flv", 
        "wml", "vrml", "swf", "dcr", "jve", "nsv", 
        "mkv", "ogm", 
        "cdg", "srt", "sub", "idx", "msmedia",
        "wvx", // This is a redirect to a wmv
        };
    
    private final String[] IMAGE =
        {
        "gif", "png",
        "jpg", "jpeg", "jpe", "jif", "jiff", "jfif",
        "tif", "tiff", "iff", "lbm", "ilbm", "eps",
        "mac", "drw", "pct", "img",
        "bmp", "dib", "rle", "ico", "ani", "icl", "cur",
        "emf", "wmf", "pcx",
        "pcd", "tga", "pic", "fig",
        "psd", "wpg", "dcx", "cpt", "mic",
        "pbm", "pnm", "ppm", "xbm", "xpm", "xwd",
        "sgi", "fax", "rgb", "ras"
        };
    
    /**
     * Array of extensions for applications that will work on any operating 
     * system.
     */
    private final String[] GENERAL_APPLICATION =
        {
        "jar", "jnlp", "iso", "bin", 
        "nrg", // Nero CD image file.
        "cue", // Another CD image file type.
        };
    
    private final String[] ARCHIVE =
        {
        "zip", "sitx", "sit", "tgz", "gz", "gzip", "bz2","rar", "lzh","lha"
        };
    
    public static final String DOCUMENT_TYPE = "document";
    public static final String AUDIO_TYPE = "audio";
    public static final String VIDEO_TYPE = "video";
    public static final String IMAGE_TYPE = "image";

    public static final String ARCHIVE_TYPE = "archive";
    public static final String APPLICATION_TYPE = "application";
    public static final String MAC_APPLICATION_TYPE = "application/mac";
    public static final String LINUX_APPLICATION_TYPE = "application/linux";
    public static final String WINDOWS_APPLICATION_TYPE = "application/win";
    
    /**
     * Creates a new translator for media types.
     */
    public ResourceTypeTranslatorImpl()
        {
        addTypes(DOCUMENTS, DOCUMENT_TYPE);
        addTypes(AUDIO, AUDIO_TYPE);
        addTypes(IMAGE, IMAGE_TYPE);
        addTypes(VIDEO, VIDEO_TYPE);
        addTypes(OSX_APPLICATIONS, MAC_APPLICATION_TYPE);
        addTypes(LINUX_APPLICATIONS, LINUX_APPLICATION_TYPE);
        addTypes(WINDOWS_APPLICATIONS, WINDOWS_APPLICATION_TYPE);
        addTypes(GENERAL_APPLICATION, APPLICATION_TYPE);
        addTypes(ARCHIVE, ARCHIVE_TYPE);
        }
    
    private void addTypes(final String[] extensions, final String type)
        {
        for (final String extension : extensions)
            {
            if (this.m_types.containsKey(extension))
                {
                LOG.error("Duplicate extension: {} in type {}",extension,type);
                }
            else
                {
                m_types.put(extension, type);
                }
            }
        }

    public String getType(final String fileName)
        {
        final String extension = 
            FilenameUtils.getExtension(fileName).toLowerCase();
        if (StringUtils.isBlank(extension) || extension.length() > 7)
            {
            LOG.debug("No extension in file name: "+fileName);
            return "unknown";
            }
        else if (!this.m_types.containsKey(extension))
            {
            // We don't know what value to assign to the extension.
            LOG.warn("No type for extension: " + extension + 
                " in file name: " + fileName);
            return "unknown";
            }
        return this.m_types.get(extension);
        }

    public boolean isAudioOrVideo(final String type)
        {
        return type.equals(AUDIO_TYPE) || type.equals(VIDEO_TYPE);
        }        
    }
