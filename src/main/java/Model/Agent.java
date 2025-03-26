package Model;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.ImageIcon;

public class Agent extends Thread implements BoardObject {
    private final String name;
    private final ImageIcon image;
    private final Square[][] board;
    public int[] pos = new int[2];
    Random random = new Random(System.currentTimeMillis());
    public static int[] dirRow = {-1, 0, 1, 0};
    public static int[] dirCol = {0, -1, 0, 1};
    public int[] posMotherShip;
    public int speed;
    public BoardObject currentObject;

    // Bandera que indica si se ha recogido una muestra
    private boolean sampleCollected = false;

    public Agent(String name, String imagePath, Square[][] board, int speed) {
        currentObject = null;
        this.speed = speed;
        this.name = name;
        System.out.println(getClass().getResource(imagePath));
        image = new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath)))
                .getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        this.board = board;
        pos[0] = random.nextInt(board.length);
        pos[1] = random.nextInt(board.length);
        board[pos[0]][pos[1]].setObject(this);
    }

    @Override
    public ImageIcon getIcon() {
        return image;
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < board.length && y >= 0 && y < board[0].length;
    }

    @Override
    public void run() {
        while (true) {
            // Si se tiene sample y se conoce la MotherShip, sigue la ruta A* para regresar
            if (sampleCollected && posMotherShip != null) {
                List<int[]> path = aStarPath(pos, posMotherShip);
                if (path != null && !path.isEmpty()) {
                    for (int[] step : path) {
                        updatePosition(step[0], step[1]);
                        try {
                            Thread.sleep(100 + speed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // Al llegar a la MotherShip, se reinicia el modo sample
                sampleCollected = false;
            } else {
                // Primero, si no se tiene sample, se revisa el sensor de migajas en celdas adyacentes
                if (!sampleCollected) {
                    List<int[]> migajaCells = new ArrayList<>();
                    for (int i = 0; i < 4; i++) {
                        int newRow = pos[0] + dirRow[i];
                        int newCol = pos[1] + dirCol[i];
                        if (inBounds(newRow, newCol)) {
                            if (board[newRow][newCol].getObject() instanceof Migaja) {
                                migajaCells.add(new int[]{newRow, newCol});
                            }
                        }
                    }
                    if (!migajaCells.isEmpty()) {
                        int[] targetCell = migajaCells.get(0);
                        // Si hay más de una migaja, se escoge la celda cuya distancia a la MotherShip sea mayor
                        if (migajaCells.size() > 1 && posMotherShip != null) {
                            int maxDistance = -1;
                            for (int[] cell : migajaCells) {
                                int distance = Math.abs(cell[0] - posMotherShip[0]) + Math.abs(cell[1] - posMotherShip[1]);
                                if (distance > maxDistance) {
                                    maxDistance = distance;
                                    targetCell = cell;
                                }
                            }
                        }
                        updatePosition(targetCell[0], targetCell[1]);
                        try {
                            Thread.sleep(100 + speed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue; // continúa el bucle sin hacer movimiento aleatorio
                    }
                }
                // Si no se detecta migaja, se procede con el movimiento aleatorio
                List<int[]> neighbors = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    int newRow = pos[0] + dirRow[i];
                    int newCol = pos[1] + dirCol[i];
                    if (inBounds(newRow, newCol)) {
                        neighbors.add(new int[]{newRow, newCol});
                    }
                }
                if (!neighbors.isEmpty()) {
                    int[] move = neighbors.get(random.nextInt(neighbors.size()));
                    updatePosition(move[0], move[1]);
                    try {
                        Thread.sleep(100 + speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public synchronized void updatePosition(int x, int y) {
        try {
            sleep(100 + speed);
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.out);
        }

        Square target = board[x][y];


        //Si voy de regreso y hay otro agente en mi camino, entonces espera a que el otro agente se mueva de lugar
        if(sampleCollected && target.getObject() instanceof Agent) {
            while (target.getObject() instanceof Agent) {
                try {
                    sleep(100 + speed);
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.out);
                }
                target = board[x][y];
            }
        }

        // Evita moverse a una celda ocupada por otro agente
        if (target.getObject() instanceof Agent || target.getObject() instanceof MotherShip || target.getObject() instanceof Obstacle) {
            return;
        }

        // Si la celda destino contiene una Sample, se recoge la muestra y se activa el modo regreso
        if (target.getObject() instanceof Sample) {
            target.minusOneFlower();
            sampleCollected = true;
            return;
        }

        // Si se está regresando y se llega a la MotherShip, se reinicia la bandera
        if (target.getObject() instanceof MotherShip && sampleCollected) {
            sampleCollected = false;
            return;
        }


        // Antes de moverse, en la celda de origen se deja (o se actualiza) una migaja si se sigue el rastro;
        // además, si la celda ya tiene 2 migajas, se reduce a 1 utilizando minusOneMigajas()
        Square origin = board[pos[0]][pos[1]];
        if (sampleCollected) {
            origin.setObject(new Migaja(2));
        } else {
            // En modo regreso, se limpia la celda de origen
            origin.eraseObject();
            origin.setObject(currentObject);
        }

        // Si el destino es una celda con migaja y el agente no tiene sample,
        // y la migaja tiene 2 unidades, se reduce a 1
        if (!sampleCollected && target.getObject() instanceof Migaja) {
            target.minusOneMigajas();
        }

        if(currentObject instanceof Agent) {
            currentObject = null;
        }

        // Actualiza la posición del agente
        currentObject = target.getObject();
        pos[0] = x;
        pos[1] = y;
        board[pos[0]][pos[1]].setObject(this);
        System.out.println(name + " -> Row: " + pos[0] + " Col: " + pos[1]);
    }

    // Implementación del algoritmo A* para encontrar la ruta desde start hasta goal
    private List<int[]> aStarPath(int[] start, int[] goal) {
        int n = board.length;

        // Estructuras para nodos abiertos y cerrados
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(nod -> nod.f));
        Map<String, Node> openMap = new HashMap<>();
        Map<String, Node> closedMap = new HashMap<>();

        String startKey = start[0] + "," + start[1];
        Node startNode = new Node(start[0], start[1], null, 0, heuristic(start[0], start[1], goal[0], goal[1]));
        open.add(startNode);
        openMap.put(startKey, startNode);

        while (!open.isEmpty()) {
            Node current = open.poll();
            String currentKey = current.row + "," + current.col;
            openMap.remove(currentKey);

            // Si llegamos a la meta, reconstruimos el camino
            if (current.row == goal[0] && current.col == goal[1]) {
                return reconstructPath(current);
            }

            // Movemos el nodo actual a cerrados
            closedMap.put(currentKey, current);

            // Expandir vecinos (movimientos en 4 direcciones)
            for (int i = 0; i < 4; i++) {
                int newRow = current.row + dirRow[i];
                int newCol = current.col + dirCol[i];

                // Verificar límites del tablero
                if (newRow < 0 || newRow >= n || newCol < 0 || newCol >= n)
                    continue;

                BoardObject obj = board[newRow][newCol].getObject();
                if (obj instanceof Sample || obj instanceof Obstacle) {
                    continue;
                }

                String neighborKey = newRow + "," + newCol;
                int tentativeG = current.g + 1;
                int h = heuristic(newRow, newCol, goal[0], goal[1]);
                int tentativeF = tentativeG + h;

                Node neighbor = new Node(newRow, newCol, current, tentativeG, h);

                // Si el vecino ya está en abiertos
                if (openMap.containsKey(neighborKey)) {
                    Node existing = openMap.get(neighborKey);
                    if (tentativeF < existing.f) {
                        // Mejor ruta encontrada: actualizar
                        existing.g = tentativeG;
                        existing.f = tentativeF;
                        existing.parent = current;
                        // Para reordenar en la cola: se remueve y se vuelve a agregar
                        open.remove(existing);
                        open.add(existing);
                    }
                    // Si no es mejor, se descarta
                    continue;
                }

                // Si el vecino ya está en cerrados
                if (closedMap.containsKey(neighborKey)) {
                    Node existing = closedMap.get(neighborKey);
                    if (tentativeF < existing.f) {
                        // Se mejora la ruta: se remueve de cerrados y se agrega a abiertos
                        closedMap.remove(neighborKey);
                        open.add(neighbor);
                        openMap.put(neighborKey, neighbor);
                    }
                    // Si no es mejor, se descarta
                    continue;
                }

                // Si no se encuentra en ninguna de las estructuras, se agrega a abiertos
                open.add(neighbor);
                openMap.put(neighborKey, neighbor);
            }
        }

        // Si se vacía la lista de abiertos sin encontrar la meta, se retorna null
        return null;
    }


    private int heuristic(int r, int c, int goalR, int goalC) {
        return Math.abs(r - goalR) + Math.abs(c - goalC);
    }

    private List<int[]> reconstructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node.parent != null) {
            path.add(0, new int[]{node.row, node.col});
            node = node.parent;
        }
        return path;
    }


    // Clase interna para representar un nodo en A*
    private class Node {
        int row, col, g, h, f;
        Node parent;

        Node(int row, int col, Node parent, int g, int h) {
            this.row = row;
            this.col = col;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
    }
}
