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
            // Si se ha recogido una muestra y se tiene definida la posición de la MotherShip,
            // calcular y seguir la ruta A* hacia la nave
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
                // Al llegar a la nave, se reinicia la bandera y se puede agregar alguna acción adicional
                sampleCollected = false;
            } else {
                // Movimiento aleatorio: se elige aleatoriamente una celda adyacente válida
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

        // Evita mover al agente si la celda destino está ocupada por otro agente
        if (target.getObject() instanceof Agent) {
            return;
        }

        // Si la celda destino contiene una Sample, recoge la muestra y activa el modo regreso
        if (target.getObject() instanceof Sample) {
            target.minusOneFlower();
            sampleCollected = true;
            return;
        }

        // Si el destino es la MotherShip y se está regresando, resetea el modo regreso
        if (target.getObject() instanceof MotherShip && sampleCollected) {
            sampleCollected = false;
            return;
        }

        // Si el destino es la MotherShip sin regresar, no se mueve
        if (target.getObject() instanceof MotherShip) {
            return;
        }

        // Antes de moverse, si se está regresando, deja una migaja en la celda de origen
        Square origin = board[pos[0]][pos[1]];
        if(sampleCollected) {
            origin.setObject(new Migaja(2));
        } else {
            origin.eraseObject();
            origin.setObject(currentObject);
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
        boolean[][] closed = new boolean[n][n];
        Node[][] nodes = new Node[n][n];

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(nod -> nod.f));

        Node startNode = new Node(start[0], start[1], null, 0,
                heuristic(start[0], start[1], goal[0], goal[1]));
        nodes[start[0]][start[1]] = startNode;
        open.add(startNode);

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.row == goal[0] && current.col == goal[1]) {
                return reconstructPath(current);
            }
            closed[current.row][current.col] = true;
            for (int i = 0; i < 4; i++) {
                int newRow = current.row + dirRow[i];
                int newCol = current.col + dirCol[i];
                if (newRow < 0 || newRow >= n || newCol < 0 || newCol >= n)
                    continue;
                if (closed[newRow][newCol])
                    continue;
                int tentativeG = current.g + 1;
                Node neighbor = nodes[newRow][newCol];
                if (neighbor == null) {
                    neighbor = new Node(newRow, newCol, current, tentativeG,
                            heuristic(newRow, newCol, goal[0], goal[1]));
                    nodes[newRow][newCol] = neighbor;
                    open.add(neighbor);
                } else if (tentativeG < neighbor.g) {
                    neighbor.g = tentativeG;
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;
                    // Debido a que PriorityQueue no reordena automáticamente,
                    // se vuelve a agregar el nodo
                    open.remove(neighbor);
                    open.add(neighbor);
                }
            }
        }
        return null; // no se encontró ruta
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
