export default function FormFields() {
    return [
        {
            id: "populationSize",
            label: "Population Size",
            type: 'number',
            bsProps: { min: 1, step: 1 },
            rules: { required: true }
        },
        {
            id: "offspringSelector",
            label: "Offspring Selector",
            type: "select",
            options: [
                {value: 'TournamentSelector', label: 'Tournament Selector'},
                {value: 'RouletteWheelSelector', label: 'Roulette Wheel Selector'}
            ],
            rules: { required: true }
        },
        {
            id: "altererFirst",
            label: "First Alterer",
            type: "select",
            options: [
                {value: 'SwapMutator', label: 'Swap Mutator'},
                {value: 'SinglePointCrossover', label: 'Single Point Crossover'},
            ],
            rules: { required: true }
        },
        {
            id: "altererFirstProbability",
            label: "First Alterer Probability",
            type: 'number',
            bsProps: { min: 0, max:1, step: 0.01 },
            rules: { required: true }
        },
        {
            id: "altererSecond",
            label: "Second Alterer",
            type: "select",
            options: [
                {value: 'SinglePointCrossover', label: 'Single Point Crossover'},
                {value: 'SwapMutator', label: 'Swap Mutator'},
            ],
            rules: { required: true }
        },
        {
            id: "altererSecondProbability",
            label: "Second Alterer Probability",
            type: 'number',
            bsProps: { min: 0, max:1, step: 0.01 },
            rules: { required: true }
        },
        {
            id: "limitBySteadyFitness",
            label: "Limit By Steady Fitness",
            type: 'number',
            bsProps: { min: 1 },
            rules: { required: true }
        },
        {
            id: "generationsLimit",
            label: "Generations Limit",
            type: 'number',
            bsProps: { min: 1 },
            rules: { required: true }
        },
    ];
}
