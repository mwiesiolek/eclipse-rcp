package com.vogella.tasks.ui.parts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class AutoCompleteFieldPart {

	private Path lastDir;

    private static class SubstringMatchContentProposalProvider implements IContentProposalProvider {

        private List<String> proposals = Collections.emptyList();

        @Override
        public IContentProposal[] getProposals(String contents, int position) {
            if (position == 0) {
                return null;
            }
            String substring = contents.substring(0, position);
            Pattern pattern = Pattern.compile(substring,
                    Pattern.LITERAL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            IContentProposal[] filteredProposals = proposals.stream()
                    .filter(proposal -> proposal.length() >= substring.length() && pattern.matcher(proposal).find())
                    .map(ContentProposal::new).toArray(IContentProposal[]::new);
            // callers expect us to return null if we don't find any matching proposal
            return filteredProposals.length == 0 ? null : filteredProposals;
        }

        public void setProposals(List<String> proposals) {
            this.proposals = proposals;
        }
    }

    private static class AutoComplete {
        private SubstringMatchContentProposalProvider proposalProvider;
        private ContentProposalAdapter adapter;

        public AutoComplete(Control control, IControlContentAdapter controlContentAdapter, List<String> proposals) {
            proposalProvider = new SubstringMatchContentProposalProvider();
            proposalProvider.setProposals(proposals);
            adapter = new ContentProposalAdapter(control, controlContentAdapter, proposalProvider, null, null);
            adapter.setPropagateKeys(true);
            adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
        }

        public void setProposals(List<String> proposals) {
            proposalProvider.setProposals(proposals);
        }
    }
	
	@PostConstruct
	public void createControls(Composite parent) {
		setUpLayout(parent);

		Combo combo = new Combo(parent, SWT.NONE);
		AutoCompleteField autoCompleteField = new AutoCompleteField(combo, new ComboContentAdapter());

		combo.addModifyListener(e -> {
			Path dir = getPathWithoutFileName(combo.getText());
			if (dir == null || dir.equals(lastDir) || !isDirectory(dir)) {
				return;
			}
			lastDir = dir;
			try (Stream<Path> paths = Files.list(dir)) {
				List<String> directories = filterPaths(paths);
				autoCompleteField.setProposals(directories.toArray(new String[directories.size()]));
			} catch (IOException ex) {
				// ignore
			}
		});
	}

	private void setUpLayout(Composite parent) {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		parent.setLayout(gridLayout);
	}

	private Path getPathWithoutFileName(String inputPath) {
		int lastIndex = inputPath.lastIndexOf(File.separatorChar);
		if (separatorNotFound(lastIndex)) {
			return null;
		} else if (endsWithSeparator(inputPath, lastIndex)) {
			return getPath(inputPath);
		} else {
			return getPath(removeFileName(inputPath, lastIndex));
		}
	}

	private boolean separatorNotFound(int lastIndex) {
		return lastIndex < 0;
	}

	private boolean endsWithSeparator(String inputPath, int lastIndex) {
		return lastIndex == inputPath.length();
	}

	private String removeFileName(String text, int lastIndex) {
		if (lastIndex == 0) {
			return File.separator;
		} else {
			return text.substring(0, lastIndex);
		}
	}

	private Path getPath(String text) {
		try {
			return Paths.get(text);
		} catch (InvalidPathException ex) {
			return null;
		}
	};

	private boolean isDirectory(Path dir) {
		try {
			return Files.isDirectory(dir);
		} catch (SecurityException ex) {
			return false;
		}
	}

	private List<String> filterPaths(Stream<Path> paths) {
		return paths.filter(path -> {
			String[] directoriesInPath = path.toString().split(File.separator);
			String fileName = directoriesInPath[directoriesInPath.length - 1];
			String lastDirectory = directoriesInPath[directoriesInPath.length - 2];
			return !lastDirectory.equals(".") && !fileName.startsWith(".") && Files.isDirectory(path);
		}).map(Path::toString).collect(Collectors.toList());
	}
}
