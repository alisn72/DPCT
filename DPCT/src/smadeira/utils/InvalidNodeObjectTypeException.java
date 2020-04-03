package smadeira.utils;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:   Copyright (C) 2007  Joana Gonçalves
 *                 This program is free software; you can redistribute
 *                 it and/or modify it under the terms of the GNU General
 *                 Public License as published by the Free Software
 *                 Foundation; either version 3 of the License, or
 *                 any later version.
 *
 *                 This program is distributed in the hope that it will
 *                 be useful, but WITHOUT ANY WARRANTY; without even the
 *                 implied warranty of MERCHANTABILITY or FITNESS FOR A
 *                 PARTICULAR PURPOSE.  See the
 *                 <a href="http://www.gnu.org/licenses/gpl.html">
 *                 GNU General Public License</a> for more details.
 * </p>
 *
 * @author Joana P. Gonçalves
 * @version 1.0
 */

public class InvalidNodeObjectTypeException extends Exception {
    /**
     * Constructs a new instance of <code>InvalidNodeObjectTypeException</code>.
     */
    public InvalidNodeObjectTypeException() {
        super();
    }

    /**
     * Constructs a new instance of <code>InvalidDataTypeException</code>
     * with the given information message.
     *
     * @param message String information message
     */
    public InvalidNodeObjectTypeException(String message) {
        super(message);
    }
}
