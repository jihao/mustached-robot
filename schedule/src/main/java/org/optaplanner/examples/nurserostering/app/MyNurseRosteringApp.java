/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.nurserostering.app;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.solver.XmlSolverFactory;
import org.optaplanner.core.impl.event.BestSolutionChangedEvent;
import org.optaplanner.core.impl.event.SolverEventListener;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.nurserostering.persistence.NurseRosteringDao;
import org.optaplanner.examples.nurserostering.persistence.NurseRosteringExporter;
import org.optaplanner.examples.nurserostering.persistence.NurseRosteringImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyNurseRosteringApp {

	public static final String SOLVER_CONFIG = "/org/optaplanner/examples/nurserostering/solver/nurseRosteringSolverConfig.xml";

	public static void main(String[] args) {
		final MyNurseRosteringApp app = new MyNurseRosteringApp();
		app.solutionBusiness
				.importSolution(new File(
						"C:/design_env/optaplanner-distribution-6.0.0.Final/examples/sources/data/nurserostering/import/toy1_mod.xml"));

		ExecutorService executor = Executors.newFixedThreadPool(1);
		final Solution planningProblem = app.solutionBusiness.getSolution(); // In event thread

		executor.submit(new Runnable() {
			@Override
			public void run() {
				Solution bestSolution = null;
				try {
					bestSolution = app.solutionBusiness.solve(planningProblem); // Not in event thread
				} catch (final Throwable e) {
					// Otherwise the newFixedThreadPool will eat the exception...
					logger.error("Solving failed.", e);
					bestSolution = null;
				}
				app.solutionBusiness.setSolution(bestSolution); // In event thread
			}

		});

		// main thead wait for 1 minute then terminate the solving result
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("get 1st best result");
		app.solutionBusiness
				.exportSolution(new File(
						"C:/design_env/optaplanner-distribution-6.0.0.Final/examples/sources/data/nurserostering/solved/toy1_mod_sol.xml"));

		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("get 2nd best result");
		app.solutionBusiness
				.exportSolution(new File(
						"C:/design_env/optaplanner-distribution-6.0.0.Final/examples/sources/data/nurserostering/solved/toy1_mod_sol.xml"));

		
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("get 3rd best result");
		app.solutionBusiness
				.exportSolution(new File(
						"C:/design_env/optaplanner-distribution-6.0.0.Final/examples/sources/data/nurserostering/solved/toy1_mod_sol.xml"));

	}
	public void registerForBestSolutionChanges() {
        solver.addEventListener(new SolverEventListener() {
            // Not called on the event thread
            public void bestSolutionChanged(BestSolutionChangedEvent event) {
                // Avoid ConcurrentModificationException when there is an unprocessed ProblemFactChange
                // because the paint method uses the same problem facts instances as the Solver's workingSolution
                // unlike the planning entities of the bestSolution which are cloned from the Solver's workingSolution
                if (solver.isEveryProblemFactChangeProcessed()) {
                    // final is also needed for thread visibility
                    final Solution latestBestSolution = event.getNewBestSolution();
                    // Migrate it to the event thread
                    Executors.newFixedThreadPool(1).submit(new Runnable() {
                        public void run() {
                            // TODO by the time we process this event, a newer bestSolution might already be queued
                            solutionBusiness.setSolution(latestBestSolution);
                            System.out.println("bestSolutionChanged");
                        }
                    });
                }
            }
        });
    }
	public MyNurseRosteringApp() {
		solutionBusiness = createSolutionBusiness();
		registerForBestSolutionChanges();
	}

	protected static final Logger logger = LoggerFactory
			.getLogger(MyNurseRosteringApp.class);

	protected SolutionBusiness solutionBusiness;
	protected Solver solver;
	public SolutionBusiness createSolutionBusiness() {
		SolutionBusiness solutionBusiness = new SolutionBusiness();
		solutionBusiness.setSolutionDao(createSolutionDao());
		solutionBusiness.setImporter(createSolutionImporter());
		solutionBusiness.setExporter(createSolutionExporter());
		solutionBusiness.updateDataDirs();
		
		solver = createSolver();
		solutionBusiness.setSolver(solver);
		return solutionBusiness;
	}

	protected Solver createSolver() {
		XmlSolverFactory solverFactory = new XmlSolverFactory();
		solverFactory.configure(SOLVER_CONFIG);
		return solverFactory.buildSolver();
	}

	protected SolutionDao createSolutionDao() {
		return new NurseRosteringDao();
	}

	protected AbstractSolutionImporter createSolutionImporter() {
		return new NurseRosteringImporter();
	}

	protected AbstractSolutionExporter createSolutionExporter() {
		return new NurseRosteringExporter();
	}

}
