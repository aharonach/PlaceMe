import useAxios from "../../../hooks/useAxios";
import {useForm} from "react-hook-form";
import {Alert} from "react-bootstrap";
import HtmlForm from "../../Forms/HtmlForm";
import FormFields from "./FormFields";
import {extractListFromAPI} from "../../../utils";
import {useNavigate, useOutletContext} from "react-router-dom";

export default function AddResult() {
    const { placement } = useOutletContext();
    const navigate = useNavigate();
    let methods = useForm({
        defaultValues: {
            name: '',
            description: '',
            amount: 1,
        }
    });

    const fields = [ ...FormFields(), {
        id: 'amount',
        label: 'Amount of results to generate',
        type: 'number',
        bsProps: { min: 1 },
        rules: { min: 1 }
    }];

    const [result, error, loading, axiosFetch] = useAxios();

    const generateResult = (data) => {
        axiosFetch({
            method: 'post',
            url: `/placements/${placement.id}/results/generate` + (data.amount ? `?amountOfResults=${data.amount}` : ''),
            data: { name: data.name, description: data.description }
        })
            .then( result =>
                extractListFromAPI(result, 'placementResultList').length > 0
                    ? navigate(`/placements/${placement.id}/results`, {replace: true}) : null
            );
    }

    return (
        <>
            <h2>Generate Result</h2>
            {!loading && error && <Alert variant="danger">{error}</Alert> }
            <HtmlForm formProps={methods} fields={fields} submitCallback={generateResult} loading={loading}></HtmlForm>
        </>
    );
}