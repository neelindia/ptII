package ptolemy.domains.ptides.lib;

import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

public class NetworkTransmitter extends EnvironmentTransmitter {
    public NetworkTransmitter(CompositeEntity container, String name)
            throws IllegalActionException, NameDuplicationException {
        super(container, name);
    }
}
