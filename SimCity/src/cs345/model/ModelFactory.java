/* This work by Christopher Reedy, email address: Chris.Reedy@wwu.edu,
 * is licensed under the Creative Commons Attribution 4.0 International
 * License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by/4.0/.
 */

package cs345.model;

import java.util.Properties;

/**
 * A factory for building Game models. An instance of this factory is based to
 * the TextView by the Main program.
 *
 * @author Chris Reedy (Chris.Reedy@wwu.edu)
 */
public class ModelFactory {

    public ModelFactory() {
        // Nothing to do here
    }

    /**
     * Create a new model object.
     * @param props the Properties object with the default properties
     * @return the model
     */
    public Cs345Opolis makeModel(Properties props) {
        if (props == null) {
            throw new IllegalArgumentException("props must be non-null");
        }
        return Cs345Opolis.newCity(props);
    }
}
