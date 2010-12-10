/**
 * Copyright (c) 2010 Martin M Reed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.hardisonbrewing.maven.core;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

public final class FileUtils extends org.codehaus.plexus.util.FileUtils {

    public static final String CURRENT_DIRECTORY_MARKER = "." + File.separator;
    public static final String PARENT_DIRECTORY_MARKER = ".." + File.separator;

    private FileUtils() {

        // do nothing
    }

    /**
     * Return the extenstion type for the specified {@link File}.
     * @param file The {@link File} to return the extenstion type of.
     * @return
     * @throws NoSuchArchiverException
     */
    public static final String getExtension( File file ) throws NoSuchArchiverException {

        return getExtension( file.getName() );
    }

    /**
     * Check if the specified file name exists.
     * @param fileName
     * @return
     */
    public static final boolean exists( String fileName ) {

        if ( fileName.startsWith( PARENT_DIRECTORY_MARKER ) ) {
            throw new IllegalStateException( "Accessing files outside the project domain is not allowed." );
        }

        if ( fileName.startsWith( CURRENT_DIRECTORY_MARKER ) ) {
            fileName = fileName.substring( CURRENT_DIRECTORY_MARKER.length() );
        }

        StringBuffer filePath = new StringBuffer();

        if ( !fileName.startsWith( File.separator ) ) {
            filePath.append( ProjectService.getBaseDir() );
        }

        filePath.append( File.separator );
        filePath.append( fileName );

        File dependencyFile = new File( filePath.toString() );
        return dependencyFile.exists();
    }

    /**
     * Convert the specified file path to a canonical path that can be used with
     * {@link ProjectService.getBaseDirPath()} as the new base path.
     * @param filePath
     * @return
     */
    public static final String getProjectCanonicalPath( String filePath ) {

        String targetDirectoryPath = TargetDirectoryService.getTargetDirectoryPath();

        if ( filePath.startsWith( targetDirectoryPath ) ) {
            return filePath.substring( targetDirectoryPath.length() );
        }

        String baseDirPath = ProjectService.getBaseDirPath();

        if ( filePath.startsWith( baseDirPath ) ) {
            return filePath.substring( baseDirPath.length() );
        }

        return filePath;
    }

    /**
     * If a file path does not exist, check the target directory and return the resolved path.
     * @param filePaths
     * @throws IllegalArgumentException If the file cannot be found.
     */
    public static final void resolveFilePaths( String[] filePaths ) {

        if ( filePaths == null ) {
            return;
        }

        for (int i = 0; i < filePaths.length; i++) {
            filePaths[i] = resolveFilePath( filePaths[i] );
        }
    }

    public static final void resolveFilePaths( List<String> filePaths ) {

        if ( filePaths == null ) {
            return;
        }

        Iterator<String> iterator = filePaths.iterator();

        for (int i = 0; iterator.hasNext(); i++) {
            filePaths.set( i, resolveFilePath( iterator.next() ) );
        }
    }

    /**
     * If a file path does not exist, check the target directory and return the resolved path.
     * @param filePath
     * @return
     * @throws IllegalArgumentException If the file cannot be found.
     */
    public static final String resolveFilePath( String filePath ) {

        if ( filePath == null ) {
            return filePath;
        }

        filePath = resolveProjectFilePath( filePath );

        if ( exists( filePath ) ) {
            return filePath;
        }

        String resolvedTargetPath = TargetDirectoryService.resolveTargetFilePath( filePath );

        if ( !exists( resolvedTargetPath ) ) {
            throw new IllegalArgumentException( "File[" + filePath + "] cannot be found." );
        }

        return resolvedTargetPath;
    }

    public static final String resolveProjectFilePath( String filePath ) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append( ProjectService.getBaseDirPath() );
        stringBuffer.append( File.separator );
        stringBuffer.append( FileUtils.getProjectCanonicalPath( filePath ) );
        return stringBuffer.toString();
    }
}
