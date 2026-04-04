package io.github.milkdrinkers.milkonomicsplugin.config.common;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.transformation.TransformAction;

import java.util.Arrays;

public final class MigrationUtil {
    /**
     * Copyright (c) 2024 GeyserMC. <a href="http://geysermc.org">geysermc.org</a>
     * <p>
     * Permission is hereby granted, free of charge, to any person obtaining a copy
     * of this software and associated documentation files (the "Software"), to deal
     * in the Software without restriction, including without limitation the rights
     * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     * copies of the Software, and to permit persons to whom the Software is
     * furnished to do so, subject to the following conditions:
     * <p>
     * The above copyright notice and this permission notice shall be included in
     * all copies or substantial portions of the Software.
     * <p>
     * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
     * THE SOFTWARE.
     * <p>
     * The base interface for config migrations.
     *
     * @author GeyserMC
     * @link <a href="https://github.com/GeyserMC/Geyser">Github</a>
     */
    public static TransformAction renameAndMove(String... newPath) {
        return ((path, value) -> Arrays.stream(newPath).toArray());
    }

    /**
     * Copyright (c) 2024 GeyserMC. <a href="http://geysermc.org">geysermc.org</a>
     * <p>
     * Permission is hereby granted, free of charge, to any person obtaining a copy
     * of this software and associated documentation files (the "Software"), to deal
     * in the Software without restriction, including without limitation the rights
     * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     * copies of the Software, and to permit persons to whom the Software is
     * furnished to do so, subject to the following conditions:
     * <p>
     * The above copyright notice and this permission notice shall be included in
     * all copies or substantial portions of the Software.
     * <p>
     * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
     * THE SOFTWARE.
     * <p>
     * The base interface for config migrations.
     *
     * @author GeyserMC
     * @link <a href="https://github.com/GeyserMC/Geyser">Github</a>
     */
    public static TransformAction moveTo(String... newPath) {
        return (path, value) -> {
            final Object[] arr = path.array();
            if (arr.length == 0) {
                throw new ConfigurateException(value, "The root node cannot be renamed!");
            } else {

                final Object[] result = new Object[newPath.length + 1]; // create a new array with space for newPath segments + the original last segment
                System.arraycopy(newPath, 0, result, 0, newPath.length);
                result[newPath.length] = arr[arr.length - 1];
                return result;
            }
        };
    }
}
