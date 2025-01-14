/*
* Copyright (C) 2021, Alexei Khatskevich
*
* Licensed under the BSD 3-Clause license.
* You may obtain a copy of the License at
*
* https://github.com/Gmugra/net.cactusthorn.config/blob/main/LICENSE
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.cactusthorn.config.extras.toml;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.cactusthorn.config.core.loader.Loader;

public class ClasspathTOMLLoaderTest {

    @BeforeAll static void setUpLogger() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.FINE);
        // switch off default Handlers to do not get anything in console
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.OFF);
        }
    }

    private static final Loader LOADER = new ClasspathTOMLLoader();
    private static final ClassLoader CL = ClasspathTOMLLoaderTest.class.getClassLoader();

    @Test public void acceptSimple() {
        assertTrue(LOADER.accept(URI.create("classpath:standard-properties.toml")));
    }

    @Test public void acceptFragment() {
        assertTrue(LOADER.accept(URI.create("classpath:standard-properties.toml#ISO-8859-1")));
    }

    @Test public void notAcceptNotOpaque() {
        assertFalse(LOADER.accept(URI.create("classpath://a.toml#ISO-8859-1")));
    }

    @Test public void notAcceptNotToml() {
        assertFalse(LOADER.accept(URI.create("classpath:a.properties#ISO-8859-1")));
    }

    @Test public void notAcceptScheme() {
        assertFalse(LOADER.accept(URI.create("mail:a.toml#ISO-8859-1")));
    }

    @Test public void load() {
        Map<String, String> properties = LOADER.load(URI.create("classpath:correct.toml#UTF-8"), CL);
        assertEquals("frontend", properties.get("servers.alpha.role"));
    }

    @Test public void loadWrong() {
        Map<String, String> properties = LOADER.load(URI.create("classpath:standard-properties-wrong.toml"), CL);
        assertTrue(properties.isEmpty());
    }
}
