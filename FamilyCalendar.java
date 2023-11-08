import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import java.util.Locale;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FamilyCalendar extends JFrame {

	String[] dayAr = { "Sun", "Mon", "Tue", "Wen", "Thur", "Fri", "Sat" };
	DateBox[] dateBoxAr = new DateBox[dayAr.length * 6];
	JPanel p_north;
	JPanel p_south;
	JButton bt_prev;
	JLabel lb_title;
	JButton bt_next;
	JPanel p_center; // 날짜 박스 처리할 영역
	JButton bt_create_event;
	JButton bt_RVSP;
	JButton bt_update_event;
	JButton bt_delete_event;
	JButton bt_eventList;
	JButton bt_modeChange;
	JButton bt_notification;
	JButton bt_createUser;
	JPanel dayPanel;
	JLabel weekLabel;
	JFrame frame2;
	JFrame frame3;
	String month;

	JPanel calendarPanel;
	Calendar cal; // 날짜 객체
	int currentWeek;
	int currentDay;
	int dayOfMonth;
	int today;
	String cellValue;
	Calendar calendar;

	int yy; // 기준점이 되는 년도
	int mm; // 기준점이 되는 월
	int ww;
	int dd;
	int startDay; // 월의 시작 요일
	int lastDate; // 월의 마지막 날
	int userID = -1; // Current user's ID
	String name = new String(""); // Current user's name

	///////////////////////////////////////// Log-in panel
	///////////////////////////////////////// ///////////////////////////////////////////////
	public void displayLoginPanel() {
		// Log-in panel here
		JFrame frame = new JFrame("Log-in to family calendar");
		frame.setLocationRelativeTo(null);
		// Create JTextField components for name and password
		JTextField nameField = new JTextField(20);
		JPasswordField passwordField = new JPasswordField(20);

		// Create JButton to submit the input
		JButton submitButton = new JButton("Log-in");

		// Create ActionListener for the submit button
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String inputName = nameField.getText();
				char[] passwordChars = passwordField.getPassword();
				String inputPasswd = new String(passwordChars);

				// Close the JFrame
				frame.dispose();

				// Now you can use inputName and inputPasswd as needed
				System.out.println("Name: " + inputName);
				System.out.println("Password: " + inputPasswd);

				if (loginIsSuccessful(inputName, inputPasswd)) {
					// displayCalendarMonthly();
					displayCalendarMonthly();
				}
			}
		});

		// Create a JPanel to hold the components
		JPanel panel = new JPanel(new GridLayout(3, 2));

		// Add components to the panel
		panel.add(new JLabel("Name: "));
		panel.add(nameField);
		panel.add(new JLabel("Password: "));
		panel.add(passwordField);
		panel.add(submitButton, BorderLayout.CENTER);

		// Add the panel to the frame
		frame.add(panel);

		// Set frame properties
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 150);
		frame.setVisible(true);
	}

	//////////////////////////////////////// Monthly
	//////////////////////////////////////// calendar//////////////////////////////////////////////
	public void displayCalendarMonthly() {
		frame2 = new JFrame("Family Calendar by Justin");
		System.out.println(String.format("Hello %s, welcome to Justin's family calendar.", name));
		System.out.println(String.format("Your user id is '%d'", userID));
		System.out.println("-------------------Monthly Mode-------------------");
		// 디자인
		p_north = new JPanel();
		p_south = new JPanel();
		bt_prev = new JButton("previous");
		lb_title = new JLabel("upcomming year", SwingConstants.CENTER);
		bt_next = new JButton("next");

		bt_create_event = new JButton("Create an event");
		bt_update_event = new JButton("Modify an event");
		bt_delete_event = new JButton("Delete an event");
		bt_RVSP = new JButton("RSVP");
		bt_notification = new JButton("Notifications");
		bt_eventList = new JButton("View event list");
		bt_createUser = new JButton("Create an account");
		bt_modeChange = new JButton("Mode change");

		p_center = new JPanel();

		// 라벨에 폰트 설정
		lb_title.setFont(new Font("Arial-Black", Font.BOLD, 25));
		lb_title.setPreferredSize(new Dimension(300, 30));

		p_north.add(bt_prev);
		p_north.add(lb_title);
		p_north.add(bt_next);
		p_south.add(bt_create_event);
		p_north.add(bt_RVSP);
		p_north.add(bt_notification);
		p_south.add(bt_update_event);
		p_south.add(bt_delete_event);
		p_south.add(bt_eventList);
		p_south.add(bt_modeChange);
		p_south.add(bt_createUser);
		frame2.add(p_north, BorderLayout.NORTH);
		frame2.add(p_south, BorderLayout.SOUTH);
		frame2.add(p_center);

		// 이전 버튼을 눌렀을 때 전 월로 이동해야함
		bt_prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateMonth(-1);
			}
		});

		// 다음 버튼을 눌렀을 때 다음 달로 이동해야함
		bt_next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateMonth(1);
			}
		});

		// Create an event
		bt_create_event.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createEvent();
			}
		});

		// Update an event
		bt_update_event.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateEvent();
			}
		});

		// Delete an event
		bt_delete_event.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteEvent();
			}
		});

		// Notification
		bt_notification.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notification();
			}
		});

		// RVSP
		bt_RVSP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendRSVP();
			}
		});

		// Event list
		bt_eventList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventList();
			}
		});

		// Mode change
		bt_modeChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modeChange(3);
			}
		});

		// User create
		bt_createUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createUser();
			}
		});

		getCurrentDate(); // 현재 날짜 객체 생성
		getDateInfo(); // 날짜 객체로부터 정보들 구하기
		setDateTitle(); // 타이틀 라벨에 날짜 표시하기
		createDay(); // 요일 박스 생성
		createDate(); // 날짜 박스 생성
		printDate(); // 상자에 날짜 그리기

		frame2.setVisible(true);
		frame2.setBounds(100, 100, 830, 780);
		// frame2.setResizable(false);

		frame2.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	//////////////////////////////////////// Weekly calendar
	//////////////////////////////////////// //////////////////////////////////////////////

	public void displayCalendarWeekly() {

		System.out.println("-------------------Weekly Mode-------------------");
		frame3 = new JFrame("Family Calendar by Justin (Weekly Mode)");
		frame3.setTitle("Weekly Calendar");
		frame3.setSize(1600, 800);
		frame3.setLayout(new BorderLayout());
		frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTable weeklyTable = new JTable();
		JPanel p_north = new JPanel();
		JPanel p_south = new JPanel();

		JPanel weekPanel = new JPanel(new BorderLayout());

		weeklyTable.setGridColor(Color.LIGHT_GRAY);

		DefaultTableModel weeklyModel = new DefaultTableModel(
				new Object[] { "Date/day", "Event 1", "Event 2", "Event 3", "Event 4", "Event 5", "Event 6" }, 0);
		weeklyTable.setModel(weeklyModel);

		calendar = Calendar.getInstance();
		// currentWeek = calendar.get(Calendar.WEEK_OF_MONTH);
		currentDay = calendar.get(Calendar.DAY_OF_MONTH);

		// JPanel mainPanel = new JPanel(new BorderLayout());

		JButton prevWeekButton = new JButton("Previous week");
		JButton nextWeekButton = new JButton("Next week");

		bt_create_event = new JButton("Create an event");
		bt_update_event = new JButton("Modify an event");
		bt_delete_event = new JButton("Delete an event");
		bt_RVSP = new JButton("RVSP");
		bt_notification = new JButton("Notifications");
		bt_eventList = new JButton("View event list");
		bt_createUser = new JButton("Create an account");
		bt_modeChange = new JButton("Mode change");

		// calendarPanel = new JPanel(new GridLayout(0, 1));

		prevWeekButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// currentWeek--;
				cal.add(Calendar.DATE, -7);
				updateWeeklyCalendar(weeklyTable, cal.getTime());
			}
		});

		nextWeekButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentWeek++;
				cal.add(Calendar.DATE, 7);
				updateWeeklyCalendar(weeklyTable, cal.getTime());
			}
		});

		// Create an event
		bt_create_event.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createEvent();
			}
		});

		// Update an event
		bt_update_event.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateEvent();
			}
		});

		// Delete an event
		bt_delete_event.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteEvent();
			}
		});

		// Notification
		bt_notification.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notification();
			}
		});

		// RVSP
		bt_RVSP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendRSVP();
			}
		});

		// Event list
		bt_eventList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventList();
			}
		});

		// Mode change
		bt_modeChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modeChange(3);
			}
		});

		// User create
		bt_createUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createUser();
			}
		});

		updateWeeklyCalendar(weeklyTable, cal.getTime());

		p_north.add(prevWeekButton);
		p_north.add(nextWeekButton);
		p_south.add(bt_create_event);
		p_north.add(bt_RVSP);
		p_north.add(bt_notification);
		p_south.add(bt_update_event);
		p_south.add(bt_delete_event);
		p_south.add(bt_eventList);
		p_south.add(bt_modeChange);
		p_south.add(bt_createUser);

		// JScrollPane scrollPane = new JScrollPane(calendarPanel);
		// comboBox

		// mainPanel.add(scrollPane, BorderLayout.CENTER);

		weekPanel.add(p_north, BorderLayout.NORTH);
		weekPanel.add(new JScrollPane(weeklyTable), BorderLayout.CENTER); // What is JScrollPane exactly for?
		weekPanel.add(p_south, BorderLayout.SOUTH);

		frame3.add(weekPanel, BorderLayout.CENTER);
		frame3.setVisible(true);

		// Update an event
		bt_update_event.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateEvent();
			}
		});

		// Delete an event
		bt_delete_event.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteEvent();
			}
		});

		// RVSP
		bt_RVSP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendRSVP();
			}
		});

		// Event list
		bt_eventList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eventList();
			}
		});

		// Mode change
		bt_modeChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modeChange(2);
			}
		});
	}

	///////////////////////////////////////// Helper methods
	///////////////////////////////////////// ///////////////////////////////////////////////

	public void getCurrentDate() {
		cal = Calendar.getInstance();
	}

	// 시작 요일, 끝 날 등 구하기
	public void getDateInfo() {
		yy = cal.get(Calendar.YEAR);
		mm = cal.get(Calendar.MONTH);
		startDay = getFirstDayOfMonth(yy, mm);
		lastDate = getLastDate(yy, mm);
	}

	// 요일 생성
	public void createDay() {
		for (int i = 0; i < 7; i++) {
			DateBox dayBox = new DateBox(dayAr[i], Color.gray, 100, 70);
			p_center.add(dayBox);
		}
	}

	// 날짜 생성
	public void createDate() {
		for (int i = 0; i < dayAr.length * 6; i++) {
			DateBox dateBox = new DateBox("", Color.LIGHT_GRAY, 100, 100);
			p_center.add(dateBox);
			dateBoxAr[i] = dateBox;
			// p_center.add(new JButton());
		}
	}

	// 해당 월의 시작 요일 구하기
	// 개발 원리 : 날짜 객체를 해당 월의 1일로 조작한 후, 요일 구하기
	// 사용 방법 : 2021년 2월을 구할시 2021, 1을 넣으면 됨
	public int getFirstDayOfMonth(int yy, int mm) {
		Calendar cal = Calendar.getInstance(); // 날짜 객체 생성
		cal.set(yy, mm, 1);
		return cal.get(Calendar.DAY_OF_WEEK) - 1;// 요일은 1부터 시작으로 배열과 쌍을 맞추기 위해 -1
	}

	// 사용 방법 : 2021년 2월을 구할시 2021, 1을 넣으면 됨
	public int getLastDate(int yy, int mm) {
		Calendar cal = Calendar.getInstance();
		cal.set(yy, mm + 1, 0);
		// 마지막 날을 의미한다.
		return cal.get(Calendar.DATE);
	}

	// 날짜 박스에 날짜 출력하기
	public void printDate() {
		System.out.println("시작 요일" + startDay);
		System.out.println("마지막 일" + lastDate);
		Calendar cal = Calendar.getInstance();
		month = cal.get(Calendar.MONTH) + "";
		int n = 1;
		for (int i = 0; i < dateBoxAr.length; i++) {
			if (i >= startDay && n <= lastDate) {

				dateBoxAr[i].day = Integer.toString(n);
				dateBoxAr[i].month = month;
				dateBoxAr[i].repaint();
				n++;
			} else {
				dateBoxAr[i].day = "";
				dateBoxAr[i].repaint();
			}
		}
	}

	// 달력을 넘기거나 전으로 이동할 때 날짜 객체에 대한 정보도 변경
	public void updateWeek(int data) {
		// 캘린더 객체에 들어있는 날짜를 기준으로 월 정보를 바꿔준다.
		cal.set(Calendar.WEEK_OF_MONTH, ww + data);
		getDateInfo();
		printDate();
		setDateTitle();
	}

	// 달력을 넘기거나 전으로 이동할 때 날짜 객체에 대한 정보도 변경
	public void updateMonth(int data) {
		// 캘린더 객체에 들어있는 날짜를 기준으로 월 정보를 바꿔준다.
		cal.set(Calendar.MONTH, mm + data);
		getDateInfo();
		printDate();
		setDateTitle();
	}

	// 몇년도 몇월인지를 보여주는 타이틀 라벨의 값을 변경
	public void setDateTitle() {
		lb_title.setText(yy + "-" + StringManager.getZeroString(mm + 1));
		lb_title.updateUI();
	}

	// For updating weekly calendar whenver the prev/next button is pressed
	public void updateWeeklyCalendar(JTable weeklyTable, Date startDate) {

		DefaultTableModel weeklyModel = (DefaultTableModel) weeklyTable.getModel();
		weeklyModel.setRowCount(0);

		int rowHeight = 100;
		weeklyTable.setRowHeight(rowHeight);

		Calendar currentCalendar = calendar.getInstance();
		currentCalendar.setTime(startDate);

		for (int i = 0; i < 7; i++) {
			int dayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);

			String dayName = currentCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
			String cellValue = dayName + " " + dayOfMonth;

			if (dayOfMonth == today
					&& currentCalendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
				cellValue = "Today: " + cellValue;
			}

			weeklyModel.addRow(new Object[] { cellValue });
			currentCalendar.add(Calendar.DATE, 1);
		}

	}

	// For checking if the input name and passwd are consistent to users DB
	public boolean loginIsSuccessful(String username, String userpassword) {

		System.out.println("Here you are");
		System.out.println(username);
		System.out.println(userpassword);
		String SQL_SELECT = String.format("SELECT user_id, name FROM users WHERE name='%s' and password='%s'", username,
				userpassword);

		try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dob", "dob",
				"dobstudio");
				PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				userID = resultSet.getInt("user_id");
				name = resultSet.getString("name");
				System.out.println(String.format("Verified ID is %s", userID));
				return true;
			}

		} catch (SQLException e) {
			System.out.print(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	////////////////////////////////////////// For buttons
	////////////////////////////////////////// ////////////////////////////////////////////////

	// After clicking create an event button
	public void createEvent() {
		JDialog createEventDialog = new JDialog();
		createEventDialog.setTitle("Create an event");

		// Create JTextField components for name and password
		JTextField title = new JTextField(30);
		JTextField startTime = new JTextField(20);
		JTextField location = new JTextField(20);
		JTextField endTime = new JTextField(20);
		JTextField description = new JTextField(100);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// Create JButton to submit the input
		JButton createButton = new JButton("Create");

		// Create ActionListener for the submit button
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String titleCreated = title.getText();
				String locationCreated = location.getText();
				String startCreated = startTime.getText();
				String endCreated = endTime.getText();
				String descCreated = description.getText();
				int creatorId = userID;

				String SQL_INSERT1 = "INSERT INTO eventinfo (title, start_time, end_time, description, creator_id, location) VALUES (?, ?, ?, ?, ?, ?);";

				try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dob",
						"dob", "dobstudio");
						PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT1)) {

					preparedStatement.setString(1, titleCreated);
					preparedStatement.setTimestamp(2, new java.sql.Timestamp(dateFormat.parse(startCreated).getTime()));
					preparedStatement.setTimestamp(3, new java.sql.Timestamp(dateFormat.parse(endCreated).getTime()));
					preparedStatement.setString(4, descCreated);
					preparedStatement.setInt(5, creatorId);
					preparedStatement.setString(6, locationCreated);

					System.out.println(preparedStatement);
					Integer rowInserted = preparedStatement.executeUpdate();
					System.out.println(rowInserted);
					// Redundant name-passwd pair will be denied

					if (rowInserted > 0) {
						System.out.println("User created successfully");
						createEventDialog.dispose();
					}

				} catch (SQLException ee) {
					System.out.print(ee.getMessage());
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});

		// Create a JPanel to hold the components
		JPanel panel = new JPanel(new GridLayout(6, 2));

		// Add components to the panel
		panel.add(new JLabel("Title: "));
		panel.add(title);
		panel.add(new JLabel("Location: "));
		panel.add(location);
		panel.add(new JLabel("Start time: "));
		panel.add(startTime);
		panel.add(new JLabel("End time: "));
		panel.add(endTime);
		panel.add(new JLabel("Description: "));
		panel.add(description);
		panel.add(createButton);

		// Add the panel to the frame
		createEventDialog.add(panel);

		// Set frame properties
		createEventDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		createEventDialog.setSize(400, 800);
		createEventDialog.setVisible(true);
	}

	// After clicking modify an event button
	public void updateEvent() {
		JDialog updateEventDialog = new JDialog();
		updateEventDialog.setTitle("Update an event");

		// Create JTextField components for name and password

		// Create JButton to submit the input
		JButton updateButton = new JButton("Update");

		// Create ActionListener for the submit button
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		// Add components to the panel
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

		JPanel event1 = new JPanel();
		JPanel event2 = new JPanel();

		event1.add(new JLabel("Interim event 1"));
		event2.add(new JLabel("Interim event 2"));

		listPanel.add(event1);
		listPanel.add(event2);

		JScrollPane scrollPanel = new JScrollPane(listPanel);
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		// Add the scrollPanel to the dialogPanel
		JPanel dialogPanel = new JPanel();
		dialogPanel.setLayout(new BorderLayout());
		dialogPanel.add(scrollPanel, BorderLayout.CENTER);

		// Add the updateButton to the dialogPanel
		dialogPanel.add(updateButton, BorderLayout.SOUTH);

		// Add the dialogPanel to the updateEventDialog
		updateEventDialog.add(dialogPanel);

		// Add the panel to the frame

		// Set frame properties
		updateEventDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		updateEventDialog.setSize(400, 800);
		updateEventDialog.setVisible(true);
	}

	// After clicking delete an event button
	public void deleteEvent() {
		JDialog deleteEventDialog = new JDialog();
		deleteEventDialog.setTitle("Delete an event");

		// Create JTextField components for name and password

		// Create JButton to submit the input
		JButton deleteButton = new JButton("Delete");
		// Create ActionListener for the submit button
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		// Add components to the panel
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

		JPanel event1 = new JPanel();
		JPanel event2 = new JPanel();

		event1.add(new JLabel("Interim event 1"));
		event2.add(new JLabel("Interim event 2"));

		listPanel.add(event1);
		listPanel.add(event2);

		JScrollPane scrollPanel = new JScrollPane(listPanel);
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		// Add the scrollPanel to the dialogPanel
		JPanel dialogPanel = new JPanel();
		dialogPanel.setLayout(new BorderLayout());
		dialogPanel.add(scrollPanel, BorderLayout.CENTER);

		// Add the updateButton to the dialogPanel
		dialogPanel.add(deleteButton, BorderLayout.SOUTH);

		// Add the dialogPanel to the updateEventDialog
		deleteEventDialog.add(dialogPanel);

		// Add the panel to the frame

		// Set frame properties
		deleteEventDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		deleteEventDialog.setSize(400, 800);
		deleteEventDialog.setVisible(true);

		// If the user is the creator, delete the event itself

		// Else, delete the user from invited people list
	}

	// After clicking RSVP button
	public void sendRSVP() {
		JDialog sendRSVPDialog = new JDialog();
		sendRSVPDialog.setTitle("Send a RSVP");

		JPanel RSVP = new JPanel();

		// Create JButton to submit the input
		JButton sendRSVPButton = new JButton("Send");
		// Create ActionListener for the submit button
		sendRSVPButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		List<Event> events = new ArrayList<>();

		String SQL_SELECT = String.format("SELECT * FROM eventinfo WHERE creator_id='%s';", userID);

		try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dob",
				"dob", "dobstudio");
				PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				int eventId = resultSet.getInt("event_id");
				String title = resultSet.getString("title");
				Timestamp startTime = resultSet.getTimestamp("start_time");
				Timestamp endTime = resultSet.getTimestamp("end_time");
				String location = resultSet.getString("location");

				Event event = new Event(eventId, title, startTime, endTime, location);
				events.add(event);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		JComboBox<Event> eventComboBox = new JComboBox<>(events.toArray(new Event[0]));
		RSVP.add(eventComboBox);

		JLabel selectedEventLabel = new JLabel("Selected Event: ");
		RSVP.add(selectedEventLabel);

		eventComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Event selectedEvent = (Event) eventComboBox.getSelectedItem();
				selectedEventLabel.setText("Selected Event: " + selectedEvent.getTitle());
			}
		});

		sendRSVPDialog.add(RSVP);

		sendRSVPDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		sendRSVPDialog.setSize(400, 800);
		sendRSVPDialog.setVisible(true);

		// If the user is the creator, delete the event itself

		// Else, delete the user from invited people list
	}

	// After clicking view event list button
	public void eventList() {
		JDialog eventListDialog = new JDialog();
		eventListDialog.setTitle("Event list");

		// Create a JPanel to hold the components
		JPanel panel = new JPanel(new GridLayout(7, 2));

		// Add the panel to the frame
		eventListDialog.add(panel);

		// Set frame properties
		eventListDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		eventListDialog.setSize(400, 800);
		eventListDialog.setVisible(true);
	}

	// After clicking notification button
	public void notification() {
		JDialog notificationDialog = new JDialog();
		notificationDialog.setTitle("Notification");

		// Create a JPanel to hold the components
		JPanel panel = new JPanel(new GridLayout(7, 2));

		// Add the panel to the frame
		notificationDialog.add(panel);

		// Set frame properties
		notificationDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		notificationDialog.setSize(400, 800);
		notificationDialog.setVisible(true);
	}

	// For changing mode between monthly/weekly
	public void modeChange(int flag) {
		if (flag == 3) { // Monthly
			// frame2.dispose();
			displayCalendarWeekly();
		} else {
			// frame3.dispose();
			displayCalendarMonthly();
		}
	}

	// After clicking create an user button
	public void createUser() {
		JDialog createUserDialog = new JDialog();
		createUserDialog.setTitle("Create an account");

		// Create JTextField components for name and password
		JTextField name = new JTextField(20);
		JPasswordField passwd = new JPasswordField(20);
		JTextField phone = new JTextField(20);
		JTextField email = new JTextField(20);

		// Create JButton to submit the input
		JButton createButton = new JButton("Create");

		// Create ActionListener for the submit button
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nameCreated = name.getText();
				String passwdCreated = new String(passwd.getPassword());
				String phoneCreated = phone.getText();
				String emailCreated = email.getText();

				String SQL_INSERT = String.format(
						"INSERT INTO users (name, password, phone, email) VALUES ('%s', '%s', '%s', '%s');",
						nameCreated,
						passwdCreated, phoneCreated, emailCreated);

				try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dob",
						"dob", "dobstudio");
						PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT)) {
					System.out.println(preparedStatement);
					Integer rowInserted = preparedStatement.executeUpdate();
					System.out.println(rowInserted);
					// Redundant name-passwd pair will be denied

					if (rowInserted > 0) {
						System.out.println("User created successfully");
						createUserDialog.dispose();
					}

				} catch (SQLException ee) {
					System.out.print(ee.getMessage());
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});

		// Create a JPanel to hold the components
		JPanel panel = new JPanel(new GridLayout(5, 2));

		// Add components to the panel
		panel.add(new JLabel("Name: "));
		panel.add(name);
		panel.add(new JLabel("Password: "));
		panel.add(passwd);
		panel.add(new JLabel("Phone:  "));
		panel.add(phone);
		panel.add(new JLabel("Email: "));
		panel.add(email);
		panel.add(createButton);

		// Add the panel to the frame
		createUserDialog.add(panel);

		// Set frame properties
		createUserDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		createUserDialog.setSize(400, 800);
		createUserDialog.setVisible(true);
	}

	class Event {
		private int eventId;
		private String title;
		private Timestamp startTime;
		private Timestamp endTime;
		private String location;

		public Event(int eventId, String title, Timestamp startTime, Timestamp endTime, String location) {
			this.eventId = eventId;
			this.title = title;
			this.startTime = startTime;
			this.endTime = endTime;
			this.location = location;
		}

		public int getEventId() {
			return eventId;
		}

		public String getTitle() {
			return title;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	public static void main(String[] args) {
		FamilyCalendar diary = new FamilyCalendar();
		diary.displayLoginPanel();
	}
}