/* 
 * Enderstone
 * Copyright (C) 2014 Sander Gielisse and Fernando van Loenhout
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.enderstone.server.permissions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OperatorLoader {

	public List<Operator> load() {
		List<Operator> list = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(new File("ops.txt")))) {
			String line;
			while ((line = reader.readLine()) != null) {
				// format: uuid:name
				list.add(new Operator(line.split(":")[1], UUID.fromString(line.split(":")[0])));
			}
		} catch (IOException e) {
		}
		return list;
	}

	public void write(List<Operator> list) {
		try (PrintWriter writer = new PrintWriter(new File("ops.txt"))) {
			for (Operator op : list) {
				writer.println(op.getUUID().toString() + ":" + op.getLastName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
