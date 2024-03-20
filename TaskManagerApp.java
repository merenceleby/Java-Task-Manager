import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.io.*;
import java.text.ParseException;

public class TaskManagerApp {
    private JFrame frame;
    private ArrayList<Task> tasks;
    private JTextField taskNameField;
    private JComboBox<String> priorityComboBox;
    private JTextField dueDateField; 
    private DefaultTableModel taskTableModel;
    private JTable taskTable; 

    public TaskManagerApp() {
        tasks = new ArrayList<>();
        initialize();
        loadTasksFromFile(); // Load tasks from file on initialization
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Task Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel taskNameLabel = new JLabel("Task Name:");
        inputPanel.add(taskNameLabel);

        taskNameField = new JTextField();
        inputPanel.add(taskNameField);

        JLabel priorityLabel = new JLabel("Priority:");
        inputPanel.add(priorityLabel);

        String[] priorities = {"Low", "Medium", "High"};
        priorityComboBox = new JComboBox<>(priorities);
        inputPanel.add(priorityComboBox);

        JLabel dueDateLabel = new JLabel("Due Date (yyyy-MM-dd):");
        inputPanel.add(dueDateLabel);

        dueDateField = new JTextField();
        inputPanel.add(dueDateField);

        frame.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });
        buttonPanel.add(addTaskButton);

        JButton deleteTaskButton = new JButton("Delete Task");
        deleteTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTask();
            }
        });
        buttonPanel.add(deleteTaskButton);

        JButton clearTaskButton = new JButton("Clear Task");
        clearTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTask();
            }
        });
        buttonPanel.add(clearTaskButton);

        frame.add(buttonPanel, BorderLayout.CENTER);

        String[] columnNames = {"Task Name", "Priority", "Due Date"};
        taskTableModel = new DefaultTableModel(columnNames, 0);
        taskTable = new JTable(taskTableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);
        frame.add(scrollPane, BorderLayout.SOUTH);

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void addTask() {
        String name = taskNameField.getText();
        String priority = (String) priorityComboBox.getSelectedItem();
        String dueDateString = dueDateField.getText();

        if (name.isEmpty() || priority.isEmpty() || dueDateString.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dueDate = dateFormat.parse(dueDateString);

                Task task = new Task(name, priority, dueDate);
                tasks.add(task);

                String[] rowData = {task.getName(), task.getPriority(), dueDateString};
                taskTableModel.addRow(rowData);

                saveTasksToFile(); // Save tasks to file after adding
                taskNameField.setText("");
                priorityComboBox.setSelectedIndex(0);
                dueDateField.setText("");
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            tasks.remove(selectedRow);
            taskTableModel.removeRow(selectedRow);
            saveTasksToFile(); // Save tasks to file after deleting
        }
    }

    private void clearTask() {
        taskNameField.setText("");
        priorityComboBox.setSelectedIndex(0);
        dueDateField.setText("");
    }

    private void loadTasksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    String priority = parts[1].trim();
                    String dueDateString = parts[2].trim();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date dueDate = dateFormat.parse(dueDateString);
                    tasks.add(new Task(name, priority, dueDate));
                    String[] rowData = {name, priority, dueDateString};
                    taskTableModel.addRow(rowData);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveTasksToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("tasks.txt"))) {
            for (Task task : tasks) {
                writer.println(task.getName() + "," + task.getPriority() + "," + task.getFormattedDueDate());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TaskManagerApp();
            }
        });
    }

    private static class Task {
        private String name;
        private String priority;
        private Date dueDate;

        public Task(String name, String priority, Date dueDate) {
            this.name = name;
            this.priority = priority;
            this.dueDate = dueDate;
        }

        public String getName() {
            return name;
        }

        public String getPriority() {
            return priority;
        }

        public String getFormattedDueDate() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(dueDate);
        }
    }
}
