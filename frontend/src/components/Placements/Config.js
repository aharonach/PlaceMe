import useFetchRecord from "../../hooks/useFetchRecord";
import {useForm} from "react-hook-form";
import {setFormValues} from "../../utils";
import HtmlForm from "../Forms/HtmlForm";
import {Alert} from "react-bootstrap";

const formFields = [
    {
        id: "populationSize",
        label: "Population Size",
        type: 'number',
        bsProps: { min: 1, step: 1 },
        rules: { required: true, valueAsNumber: true }
    },
    {
        id: "offspringSelector",
        label: "Offspring Selector",
        type: "select",
        options: [
            {value: 'TournamentSelector', label: 'Tournament Selector'},
        ],
        rules: { required: true }
    },
];

export default function Config() {
    const form = useForm();
    const [configs, error, loading] = useFetchRecord({
        fetchUrl: '/placements/configs',
        thenCallback: (configs) => {
            console.log(configs);
            setFormValues(form, configs)
        }
    });

    const handleSubmit = (data) => {
        console.log(data);
    }

    return (
        <>
            <h1>Configure Placing Algorithm</h1>
            {error && <Alert variant="danger">{error}</Alert>}
            <HtmlForm
                formProps={form}
                fields={formFields}
                loading={loading}
                submitCallback={handleSubmit}
                submitLabel={"Update"}
            />
        </>
    )
}