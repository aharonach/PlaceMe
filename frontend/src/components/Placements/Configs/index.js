import useFetchRecord from "../../../hooks/useFetchRecord";
import {useForm} from "react-hook-form";
import {setFormValues} from "../../../utils";
import HtmlForm from "../../Forms/HtmlForm";
import {Alert} from "react-bootstrap";
import FormFields from "./FormFields";
import {useState} from "react";

export default function Configs() {
    const form = useForm();
    const url = '/placements/configs';
    const [configs, error, loading, fetch] = useFetchRecord({
        fetchUrl: url,
        thenCallback: (configs) => setFormValues(form, configs)
    });
    const [updated, setUpdated] = useState(false);

    const handleSubmit = async (data) => {
        setUpdated(false);

        const response = await fetch({
            method: 'post',
            url: url,
            data: data
        });

        if ( response ) {
            setUpdated(true);
        }
    }

    return (
        <>
            <h1>Configure Placing Algorithm</h1>
            {error && <Alert variant="danger">{error}</Alert>}
            {updated && <Alert variant="success">Algorithm updated successfully</Alert>}
            <HtmlForm
                formProps={form}
                fields={FormFields()}
                loading={loading}
                submitCallback={handleSubmit}
                submitLabel={"Update"}
            />
        </>
    )
}