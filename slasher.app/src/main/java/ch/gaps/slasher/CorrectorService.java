/*
 * The MIT License Copyright 2017 HEIG-VD // SIPA. Permission is hereby granted,
 * free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions: The above copyright notice and this
 * permission notice shall be included in all copies or substantial portions of
 * the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package ch.gaps.slasher;

import ch.gaps.slasher.corrector.Corrector;

import java.util.LinkedList;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CorrectorService {
    /**
     * The {@link CorrectorService}.
     */
    private static CorrectorService service;
    /**
     * The {@link ServiceLoader} that loads {@link Corrector}s.
     */
    private final ServiceLoader<Corrector> loader;

    /**
     * Constructs a new {@link CorrectorService} instance. This constructor is
     * private to be only call by the singleton pattern.
     */
    private CorrectorService() {
        loader = ServiceLoader.load(Corrector.class);
    }

    /**
     * @return the unique instance of {@link CorrectorService}.
     */
    public static synchronized CorrectorService instance() {
        if (service == null)
            service = new CorrectorService();
        return service;
    }

    /**
     * @return all {@link Corrector}s that have been loaded by this service.
     */
    public LinkedList<Corrector> getAll() {
        LinkedList<Corrector> l = new LinkedList<>();
        try {
            for (Corrector d : loader) l.add(d);
        } catch (ServiceConfigurationError serviceError) {
            Logger.getLogger(CorrectorService.class.getName()).log(Level.SEVERE, null,
                    serviceError);
        }
        return l;
    }
}
