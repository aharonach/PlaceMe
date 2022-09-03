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

    const [result, error, loading, axiosFetch] = useAxios();

    const generateResult = (data) => {
        axiosFetch({
            method: 'post',
            url: `/placements/${placement.id}/results/generate` + (data.amount ? `?amountOfResults=${data.amount}` : ''),
            data: { name: data.name, description: data.description }
        })
            .then( result => {
                const list = extractListFromAPI(result, 'placementResultList');

                if (list.length > 0) {
                    navigate(`/placements/${placement.id}/results`, {replace: true});
                }
            });
    }

    return (
        <>
            <h2>Generate Result</h2>
            {!loading && error && <Alert variant="danger">{error}</Alert> }
            <HtmlForm formProps={methods} fields={FormFields()} submitCallback={generateResult} loading={loading}></HtmlForm>
        </>
    );
}