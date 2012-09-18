package com.anthavio.kitty.swing;

import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.anthavio.kitty.tool.ToolUtils;

/**
 * @author vanek
 * 
 * We need to sort directory listings and it is terribly ineffecient to do it in every 
 * {@link #getIndexOfChild(Object, Object)} and {@link #getChild(Object, int)} call
 * Hopefully tree rendering is sequential and we can cache sorted listing in {@link #childrenCache}
 * 
 */
public class FilesystemTreeModel implements TreeModel {

	private final File root;

	private FileFilter filter; //can be null

	private final Vector<TreeModelListener> listeners = new Vector<TreeModelListener>();

	private File[] childrenCache;

	public FilesystemTreeModel() {
		this.root = new SystemRootFile("System");
	}

	public FilesystemTreeModel(File root) {
		this.root = root;
	}

	public FilesystemTreeModel(File root, FileFilter filter) {
		this.root = root;
		this.filter = filter;
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {
		File file = (File) node;
		return file.isFile();
	}

	public int getChildCount(Object parent) {
		File file = (File) parent;
		if (file.isDirectory()) {
			childrenCache = ToolUtils.fileList(file, filter);
			return childrenCache.length;
		}
		return 0;
	}

	public int getIndexOfChild(Object parent, Object child) {
		File file = (File) child;
		for (int i = 0; i < childrenCache.length; i++) {
			if (file.getName().equals(childrenCache[i].getName())) {
				return i;
			}
		}
		return -1;
	}

	public Object getChild(Object parent, int index) {
		return childrenCache[index];
	}

	public void valueForPathChanged(TreePath path, Object value) {
		File oldFile = (File) path.getLastPathComponent();
		String fileParentPath = oldFile.getParent();
		String newFileName = (String) value;
		File targetFile = new File(fileParentPath, newFileName);
		oldFile.renameTo(targetFile);
		File parent = new File(fileParentPath);
		int[] changedChildrenIndices = { getIndexOfChild(parent, targetFile) };
		Object[] changedChildren = { targetFile };
		fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);

	}

	private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
		TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
		Iterator<TreeModelListener> iterator = listeners.iterator();
		TreeModelListener listener = null;
		while (iterator.hasNext()) {
			listener = iterator.next();
			listener.treeNodesChanged(event);
		}
	}

	public void addTreeModelListener(TreeModelListener listener) {
		listeners.add(listener);
	}

	public void removeTreeModelListener(TreeModelListener listener) {
		listeners.remove(listener);
	}

	private static class SystemRootFile extends File {

		private static final long serialVersionUID = 1L;

		private final String name;
		private final File[] rootFiles;
		private final String[] rootNames;

		public SystemRootFile(String name) {
			super(name);
			this.name = name;
			FileSystemView view = FileSystemView.getFileSystemView();
			File[] roots = File.listRoots();
			List<File> lOkRoots = new ArrayList<File>();
			for (File root : roots) {
				if (view.isFloppyDrive(root)) {
					// skip floppy drives
					continue;
				}
				lOkRoots.add(root);
			}

			rootFiles = new File[lOkRoots.size()];
			lOkRoots.toArray(rootFiles);
			rootNames = new String[rootFiles.length];
			for (int i = 0; i < rootFiles.length; i++) {
				rootNames[i] = rootFiles.toString();
			}
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public boolean isDirectory() {
			return true;
		}

		@Override
		public boolean isFile() {
			return false;
		}

		@Override
		public String[] list() {
			return rootNames;
		}

		@Override
		public String[] list(FilenameFilter filter) {
			return rootNames;
		}

		@Override
		public File[] listFiles() {
			return rootFiles;
		}

		@Override
		public File[] listFiles(FilenameFilter filter) {
			return rootFiles;
		}

		@Override
		public File[] listFiles(FileFilter filter) {
			return rootFiles;
		}

	}

	private static class FileTreeRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 1L;

		FileSystemView view = FileSystemView.getFileSystemView();

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			File file = (File) value;
			setIcon(view.getSystemIcon(file));
			if (view.isDrive(file)) {
				setText(file.toString());
				//setText(view.getSystemDisplayName(file));
			} else {
				setText(file.getName());
			}
			return this;
		}
	}

	private static class AcceptAllFilter implements FileFilter, FilenameFilter {

		public boolean accept(File pathname) {
			return true;
		}

		public boolean accept(File dir, String name) {
			return true;
		}

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Filesystem Tree");
		JTree tree = new JTree(new FilesystemTreeModel());
		tree.setCellRenderer(new FileTreeRenderer());
		frame.add(new JScrollPane(tree));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 500);
		frame.setVisible(true);
	}
}
