import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px

pio.kaleido.scope.mathjax = None

weightB1 = [63.15, 62.77]
weightB2 = [73.15, 73.55]
weightB3 = [49.15, 49.55]
weightT = [63.15, 63.15]
date = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30]

central = 'rgb(100, 30, 22)'
disco = 'rgb(21, 67, 97)'
scotty = 'rgb(126, 81, 10)'
desis = 'rgb(21, 90, 50)'



fig = go.Figure()

fig.add_trace(go.Scatter(name="预测值1", x=date, mode='lines+markers'
                         , y=weightB1
                         , line=dict(color=central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name="正常女性体重", x=date, mode='lines+markers'
                         , y=weightB2
                         , line=dict(color=disco, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name="不正常女性体重", x=date, mode='lines+markers'
                         , y=weightB3
                         , line=dict(color=scotty, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name="唐佳的体重", x=date, mode='lines+markers'
                         , y=weightT
                         , line=dict(color=desis, width=7), marker=dict(size=25, symbol='cross')))

# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6,
# marker=dict(size=10, symbol='triangle-up'))))

#legend
fig.update_layout(
    legend=dict(
        orientation="h",
        yanchor="top",
        y=1.05,
        xanchor="left",
        x=-0.05,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=23,
            color="black"
        ),
    ),
    # yaxis=dict(
    #     title_text="体重/Kg",
    #     titlefont=dict(size=15),
    #     ticktext=["0", "20", "50", "70", "90", "100"],
    #     tickvals=[0, 20, 50, 70, 90, 100],
    #     range=[0, 110],
    #     tickmode="array",
    # ),
    yaxis=dict(
        title_text="体重/Kg",
        titlefont=dict(size=15),
        ticktext=[45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65],
        tickvals=[45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65],
        range=[45, 65.5],
        tickmode="array",
    ),
    xaxis=dict(
        title_text="日期/day",
        titlefont=dict(size=15),
        ticktext=["8.2", "8.3", "8.4", "8.5", "8.6", "8.7", "8.8", "8.9", "8.10", "8.11", "8.12", "8.13", "8.14", "8.15", "8.16"
            , "8.17", "8.18", "8.19", "8.20", "8.21", "8.22", "8.23", "8.24", "8.25", "8.26", "8.27", "8.28", "8.29", "8.30", "8.31"],
        tickvals=[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30],
        range=[0, 31],
        tickmode="array",
    )
)

# size & color
fig.update_layout(
    autosize=False,
    width=1260,
    height=940,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)',
    margin=dict(
        l=5,
        r=5,
        b=5,
        t=5,
        pad=0
    ),
)
fig.update_xaxes(showline=True, linewidth=3, linecolor='black', mirror=True
                 )
fig.update_yaxes(showline=True, linewidth=3, linecolor='black', mirror=True
                 )
fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')
fig.show()
if not os.path.exists("E:\weight"):
    os.mkdir("E:\weight")

# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\weight\weight.pdf")
pio.write_image(fig, "E:\weight\weight.svg")